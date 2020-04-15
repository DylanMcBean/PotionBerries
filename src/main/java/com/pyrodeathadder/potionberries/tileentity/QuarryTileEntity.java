package com.pyrodeathadder.potionberries.tileentity;

import com.pyrodeathadder.potionberries.PotionBerries;
import com.pyrodeathadder.potionberries.init.BlockInit;
import com.pyrodeathadder.potionberries.init.ModTileEntityType;
import com.pyrodeathadder.potionberries.util.helpers.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static com.pyrodeathadder.potionberries.objects.blocks.Quarry.FACING;
import static com.pyrodeathadder.potionberries.objects.blocks.QuarryFrame.SECTION;

public class QuarryTileEntity extends TileEntity implements ITickableTileEntity {

    public int xoffset, yoffset, zoffset, xpos, ypos, zpos, tick;
    public boolean positiveX,positiveZ;
    public boolean initialised, running = false;
    public BlockPos previousBlockBreak;
    public boolean xAligned;
    public int width = (int)(Math.random()*30)+2;
    public int length = (int)(Math.random()*30)+2;
    ArrayList<Item> allowedItems;
    public TileEntity quarryStorage;
    public String facingPositon;
    public ArrayList<ItemStack> quarryInternalStorage = new ArrayList<ItemStack>();
    private int tickGoal = 100;
    private boolean movingToNextBlock;
    private BlockPos nextBlockPosition;
    public boolean finished = false;

    public QuarryTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public QuarryTileEntity() {
        this(ModTileEntityType.QUARRY.get());
    }

    @Override
    public void tick() {
        if (!initialised) init(2, 0);
        else {
            if(finished) removeStructure();
            running = quarryRunnable();
            if (running) {
                tick++;
                if (tick >= tickGoal) {
                    tick = 0;
                    if ((this.ypos + this.yoffset) > 0) execute();
                }
            }
        }


        //try and remove items inside internalStorage buffer
        if(quarryInternalStorage.size() > 0) {
            PotionBerries.LOGGER.info("Storage full");
            ItemStack stack = quarryInternalStorage.get(0);
            quarryInternalStorage.remove(0);

            quarryStorage.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(it -> {
                ItemStack te = ItemHandlerHelper.insertItemStacked(it,new ItemStack(stack.getItem(),stack.getCount()),false);
                if (te != ItemStack.EMPTY)  quarryInternalStorage.add(te);
            });
        }
    }

    private void init(int DistanceAway, int DistanceRight) {
        if(!(world instanceof ServerWorld)) return;
        int a = DistanceAway;
        int b = DistanceRight;
        TileEntity te = world.getTileEntity(this.pos.add(0, 1, 0));
        if (te != null && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).isPresent()) {
            this.initialised = true;
            this.running = true;
            facingPositon = this.getBlockState().get(FACING).getName();
            switch (facingPositon) {
                case "south":
                    xpos = this.pos.getX() - b;
                    zpos = this.pos.getZ() + a;
                    xAligned = true;
                    break;
                case "west":
                    xpos = this.pos.getX() - a;
                    zpos = this.pos.getZ() - b;
                    xAligned = false;
                    break;
                case "north":
                    xpos = this.pos.getX() + b;
                    zpos = this.pos.getZ() - a;
                    xAligned = true;
                    break;
                case "east":
                    xpos = this.pos.getX() + a;
                    zpos = this.pos.getZ() + b;
                    xAligned = false;
                    break;
            }
            ypos = this.pos.getY();
            xoffset = 0;
            yoffset = 4;
            zoffset = 0;
            positiveX = true;
            positiveZ = true;
            quarryStorage = null;
            tick = 0;
            previousBlockBreak = this.getPos();
            createStructure();
        }
    }

    private void execute() {
        BlockPos posToBreak = new BlockPos(0, 0, 0);
        switch (facingPositon) {
            case "south":
                posToBreak = new BlockPos(this.xpos + xoffset, yoffset + this.ypos, this.zpos + zoffset);
                break;
            case "west":
                posToBreak = new BlockPos(this.xpos - zoffset, yoffset + this.ypos, this.zpos + xoffset);
                break;
            case "north":
                posToBreak = new BlockPos(this.xpos - xoffset, yoffset + this.ypos, this.zpos - zoffset);
                break;
            case "east":
                posToBreak = new BlockPos(this.xpos + zoffset, yoffset + this.ypos, this.zpos - xoffset);
                break;
        }
        if(!movingToNextBlock) destroyBlock(posToBreak, true, null);
        previousBlockBreak = new BlockPos(xoffset,yoffset,zoffset);
        moveBreakPosition();
        spawnArm();
    }

    private BlockPos quarryBlockRelative(BlockPos offset) {
        if (world instanceof ServerWorld) {
            switch (this.facingPositon) {
                case "south":
                    return new BlockPos(this.xpos + offset.getX(), offset.getY() + this.ypos, this.zpos + offset.getZ());
                case "west":
                    return new BlockPos(this.xpos - offset.getZ(), offset.getY() + this.ypos, this.zpos + offset.getX());
                case "north":
                    return new BlockPos(this.xpos - offset.getX(), offset.getY() + this.ypos, this.zpos - offset.getZ());
                case "east":
                    return new BlockPos(this.xpos + offset.getZ(), offset.getY() + this.ypos, this.zpos - offset.getX());
            }
            return offset;
        }
        return null;
    }

    private void spawnArm() {
        World world = getWorld();
        if (world instanceof ServerWorld && !finished) {

            //CLEAR BLOCKS
            for (int x = 0; x < this.width; x++) { world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, previousBlockBreak.getZ())), Blocks.AIR.getDefaultState()); }
            for (int z = 0; z < this.length; z++) { world.setBlockState(quarryBlockRelative(new BlockPos(previousBlockBreak.getX(), 5, z)), Blocks.AIR.getDefaultState()); }
            for (int y = yoffset + 1; y < 5; y++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(previousBlockBreak.getX(), y, previousBlockBreak.getZ())), Blocks.AIR.getDefaultState());
            }

            for (int x = 0; x < this.width; x++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, zoffset)), BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, xAligned? 1:0)); //Top Arm E,W
            }
            for (int z = 0; z < this.length; z++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, 5, z)), BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, xAligned? 0:1)); //Top Arm N,S
            }
            for (int y = yoffset + 1; y < 5; y++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, y, zoffset)), BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, 3)); //Extending Arm
            }

            world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, yoffset + 1, zoffset)), BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, 4)); //Top Arm Connection
            world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, 5, zoffset)), BlockInit.QUARRYFRAME.get().getDefaultState().with(SECTION, 2)); //Arm Tip
        }
    }

    private void moveBreakPosition() {
        if(movingToNextBlock) {
            if(xoffset > nextBlockPosition.getX() || xoffset < nextBlockPosition.getX()) {
                if (xoffset > nextBlockPosition.getX()){
                    xoffset --;
                } else if (xoffset < nextBlockPosition.getX()){
                    xoffset ++;
                }
            } else if(zoffset > nextBlockPosition.getZ() || zoffset < nextBlockPosition.getZ()) {
                if (zoffset > nextBlockPosition.getZ()){
                    zoffset --;
                } else if (zoffset < nextBlockPosition.getZ()){
                    zoffset ++;
                }
            } else if(yoffset > nextBlockPosition.getY()) {
                yoffset --;
            }
        } else {
            //xoffset = 0;
            //zoffset = 0;
            if(!finished) {
                findNextBlock();
            }
        }

        if(!finished && nextBlockPosition.getX() == xoffset && nextBlockPosition.getY() == yoffset && nextBlockPosition.getZ() == zoffset) {
            //Calculate tickGoal for nextBlock
            assert world != null;
            BlockState blockState = world.getBlockState(quarryBlockRelative(new BlockPos(xoffset, yoffset, zoffset)));
            float Hardness = blockState.getBlockHardness(world, quarryBlockRelative(new BlockPos(xoffset, yoffset, zoffset)));
            int harvest = blockState.getHarvestLevel();
            int newTickRate = (int) (((Math.max(1, harvest) / 6f) + (Hardness / 80f)) * 50f);
            tickGoal = (blockState.isAir(world, pos) || !blockState.getFluidState().isEmpty()) ? 1 : newTickRate;
            movingToNextBlock = false;
            nextBlockPosition = null;

        }
    }

    private void findNextBlock() {
        int xoff = 0;//this.xoffset;
        int yoff;
        if (yoffset % 2 == 0) yoff = this.yoffset < 4? yoffset + 1: yoffset;
        else if (this.yoffset < 3) yoff = yoffset + 2;
        else yoff = 4;

        int zoff = 0;//this.zoffset;
        boolean px = true;
        boolean pz = true;

        BlockState blockState = world.getBlockState(quarryBlockRelative(new BlockPos(xoff, xoff, xoff)));
        while(((blockState.isAir(world, pos)||!blockState.getFluidState().isEmpty())||!(blockState.getBlock() != BlockInit.QUARRYFRAME.get()) || blockState.getBlockHardness(world, pos) < 0) && this.ypos + yoff > 0){
            if (xoff == this.width - 1 && px || xoff == 0 && !px) {
                px = !px;
                if (zoff == this.length - 1 && pz || zoff == 0 && !pz) {
                    pz = !pz;
                    yoff--;
                } else {
                    zoff += pz ? 1: -1;
                }
            } else {
                xoff += px ? 1: -1;
            }

            blockState = world.getBlockState(quarryBlockRelative(new BlockPos(xoff, yoff, zoff)));
        }
        if (this.ypos + yoff < 3 && blockState.getBlockHardness(world, pos) < 0) {
            this.finished = true;
        } else {
            nextBlockPosition = new BlockPos(xoff, yoff, zoff);
            movingToNextBlock = true;
            tickGoal = 1;
        }
    }

    private boolean quarryRunnable() {
        if (quarryInternalStorage.size() > 0) return false;
        assert world != null;
        if (world.getTileEntity(this.pos.add(0, 1, 0)) == null) { //check if there is a storage container above the quarry
            quarryStorage = null;
            return false;
        } else if (quarryStorage == null && world.getTileEntity(this.pos.add(0, 1, 0)) != null) { //if the quarry storage is null then set it
            quarryStorage = world.getTileEntity(this.pos.add(0, 1, 0));
            return true;
        }
        return true;
    }

    private boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir(world, pos) || blockState.getBlockHardness(world, pos) < 0) return false;
        else {
            IFluidState iFluidState = world.getFluidState(pos);
            if (dropBlock) {
                TileEntity tileEntity = blockState.hasTileEntity() ? world.getTileEntity(pos) : null;
                final Object holder = quarryStorage; //Make a copy of the chest beforehand
                if (!(world instanceof ServerWorld)) return false;
                Block.getDrops(blockState, (ServerWorld) world, pos, tileEntity, entity, ItemStack.EMPTY).forEach(drop -> { //get the drops for the block you have broken and loop through them
                    quarryStorage.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(it -> {
                        ItemStack te = ItemHandlerHelper.insertItem(it,new ItemStack(drop.getItem(),1),false);
                        if (te != ItemStack.EMPTY)  quarryInternalStorage.add(te);
                    });
                });
            }
            world.playEvent(2001, pos, Block.getStateId(blockState)); //play sound of block break
            return world.setBlockState(pos, iFluidState.getBlockState(), 3); //update block that has to be broken
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("initvalues", NBTHelper.toNBT(this));
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        CompoundNBT initValues = compound.getCompound("initvalues");
        if(initValues != null) {
            this.xpos = initValues.getInt("xpos");
            this.ypos = initValues.getInt("ypos");
            this.zpos = initValues.getInt("zpos");
            this.xoffset = initValues.getInt("xoff");
            this.yoffset = initValues.getInt("yoff");
            this.zoffset = initValues.getInt("zoff");
            this.tick = initValues.getInt("tick");
            this.running = initValues.getBoolean("running");
            this.positiveX = initValues.getBoolean("positiveX");
            this.positiveZ = initValues.getBoolean("positiveZ");
            this.previousBlockBreak = new BlockPos(initValues.getInt("previousX"), 0, initValues.getInt("previousZ"));
            this.width = initValues.getInt("Width");
            this.length = initValues.getInt("Length");
            this.xAligned = initValues.getBoolean("xAligned");
            this.facingPositon = initValues.getString("FacingPositon");
            this.initialised = true;
        }
    }

    public void createStructure() {
        for (int y = 0; y < 5; y++) {
            world.setBlockState(quarryBlockRelative(new BlockPos(-1, y, -1)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
            world.setBlockState(quarryBlockRelative(new BlockPos(width, y, -1)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
            world.setBlockState(quarryBlockRelative(new BlockPos(-1, y, length)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
            world.setBlockState(quarryBlockRelative(new BlockPos(width, y, length)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
        }

        for (int x = -1; x < width+1; x++) {
            world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, -1)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
            world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, length)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
        }

        for (int z = 0; z < length; z++) {
            world.setBlockState(quarryBlockRelative(new BlockPos(-1, 5, z)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
            world.setBlockState(quarryBlockRelative(new BlockPos(width, 5, z)), BlockInit.QUARRY_STRUCTURE_FRAME.get().getDefaultState());
        }
    }

    public void removeStructure() {
        if(this.initialised) {
            PotionBerries.LOGGER.info("Removing Quarry");
            PotionBerries.LOGGER.info(this.width);
            PotionBerries.LOGGER.info(this.length);
            PotionBerries.LOGGER.info(this.facingPositon);
            PotionBerries.LOGGER.info(this.ypos + yoffset);

            for (int x = 0; x < this.width; x++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, zoffset)), Blocks.AIR.getDefaultState());
            }
            for (int z = 0; z < this.length; z++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, 5, z)), Blocks.AIR.getDefaultState());
            }
            for (int y = yoffset + 1; y < 5; y++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, y, zoffset)), Blocks.AIR.getDefaultState());
            }

            for (int y = 0; y < 5; y++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(-1, y, -1)), Blocks.AIR.getDefaultState());
                world.setBlockState(quarryBlockRelative(new BlockPos(width, y, -1)), Blocks.AIR.getDefaultState());
                world.setBlockState(quarryBlockRelative(new BlockPos(-1, y, length)), Blocks.AIR.getDefaultState());
                world.setBlockState(quarryBlockRelative(new BlockPos(width, y, length)), Blocks.AIR.getDefaultState());
            }

            for (int x = -1; x < width + 1; x++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, -1)), Blocks.AIR.getDefaultState());
                world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, length)), Blocks.AIR.getDefaultState());
            }

            for (int z = 0; z < length; z++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(-1, 5, z)), Blocks.AIR.getDefaultState());
                world.setBlockState(quarryBlockRelative(new BlockPos(width, 5, z)), Blocks.AIR.getDefaultState());
            }
        }
    }

}
