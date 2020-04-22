package com.pyrodeathadder.potionberries.tileentity;

import com.pyrodeathadder.potionberries.PotionBerries;
import com.pyrodeathadder.potionberries.container.QuarryContainer;
import com.pyrodeathadder.potionberries.init.BlockInit;
import com.pyrodeathadder.potionberries.init.ModTileEntityType;
import com.pyrodeathadder.potionberries.util.helpers.NBTHelper;
import com.pyrodeathadder.potionberries.util.packets.PotionBerriesPacketHandler;
import com.sun.jna.Library;
import com.sun.jna.platform.win32.WinNT;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

import static com.pyrodeathadder.potionberries.objects.blocks.Quarry.FACING;
import static com.pyrodeathadder.potionberries.objects.blocks.Quarry.TEXTURE;
import static com.pyrodeathadder.potionberries.objects.blocks.QuarryFrame.SECTION;

public class QuarryTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public float xoffset, yoffset, zoffset, xpos, ypos, zpos, tick;
    public int gridXoffset, gridZoffset, gridSize;
    public boolean positiveX, positiveZ;
    public boolean initialised, running = false;
    public BlockPos previousBlockBreak;
    public boolean xAligned;
    public int width = 64;//(int)(Math.random()*14)+2;
    public int length = 64;//(int)(Math.random()*14)+2;
    public TileEntity quarryStorage;
    public String facingPositon;
    public ArrayList<ItemStack> quarryInternalStorage = new ArrayList<ItemStack>();
    public static HashMap<String, Integer> quarryBlocksMinned = new HashMap<>();
    public static ArrayList<String> voidItemNames = new ArrayList<String>();
    private int tickGoal = 100;
    public boolean movingToNextBlock;
    public BlockPos nextBlockPosition;
    public boolean finished = false;

    public QuarryTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public QuarryTileEntity() {
        this(ModTileEntityType.QUARRY.get());
    }

    @Override
    public void tick() {
        if (!initialised) return;
        else {
            if (finished) {
                removeStructure();
                initialised = false;
            }
            running = quarryRunnable();
            CheckTextureChange();
            if (running) {
                tick++;
                if (tick >= tickGoal) {

                    tick = 0;
                    if ((this.ypos + this.yoffset) > 0) execute();
                }
            }
        }

        //try and remove items inside internalStorage buffer
        if (quarryStorage != null && quarryInternalStorage.size() > 0) {
            ItemStack stack = quarryInternalStorage.get(0);
            quarryInternalStorage.remove(0);
            quarryStorage.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(it -> {
                ItemStack te = ItemHandlerHelper.insertItemStacked(it, new ItemStack(stack.getItem(), stack.getCount()), false);
                if (te != ItemStack.EMPTY) quarryInternalStorage.add(te);
            });
        }


        //send packets to update renderer
        if (world instanceof ServerWorld) {
            PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(this.getPos())).send(new SUpdateTileEntityPacket(this.getPos(), 0, this.getUpdateTag())); // .sendTo(new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag()),manager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public void CheckTextureChange() {
        if (!this.initialised) {
            world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(TEXTURE, 0));
        } else {
            if (!running && quarryStorage == null && quarryInternalStorage.size() != 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 1));
            } else if (!running && quarryStorage == null && quarryInternalStorage.size() == 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 2));
            } else if (!running && quarryStorage != null && quarryInternalStorage.size() != 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 3));
            } else if (!running && quarryStorage != null && quarryInternalStorage.size() == 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 4));
            } else if (running && quarryStorage == null && quarryInternalStorage.size() != 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 5));
            } else if (running && quarryStorage == null && quarryInternalStorage.size() == 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 6));
            } else if (running && quarryStorage != null && quarryInternalStorage.size() != 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 7));
            } else if (running && quarryStorage != null && quarryInternalStorage.size() == 0) {
                world.setBlockState(this.getPos(), BlockInit.QUARRY.get().getDefaultState().with(FACING, getBlockState().get(FACING)).with(TEXTURE, 8));
            }
        }
    }


    ///Inventory -- Void Items
    public final ItemStackHandler inventory = new ItemStackHandler(12) {
        @Override
        public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
            return true;
        }

        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);
            QuarryTileEntity.this.markDirty();
        }
    };

    public void init(int DistanceAway, int DistanceRight) {
        if (!(world instanceof ServerWorld)) return;
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
            gridXoffset = 0;
            gridZoffset = 0;
            gridSize = 32;
            positiveX = true;
            positiveZ = true;
            quarryStorage = null;
            previousBlockBreak = this.getPos();
            createStructure();
            tick = 0;
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
        if (!movingToNextBlock) destroyBlock(posToBreak, true, null);
        moveBreakPosition();
        //spawnArm();
    }

    public BlockPos quarryBlockRelative(BlockPos offset) {
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

    private void moveBreakPosition() {
        if(movingToNextBlock) {

            Vector3f p = new Vector3f(nextBlockPosition.getX() - xoffset,nextBlockPosition.getY() - yoffset,nextBlockPosition.getZ() - zoffset);

            if(Math.abs((p.getX()+p.getY()+p.getZ())/3) < 0.07) {
                this.xoffset = nextBlockPosition.getX();
                this.yoffset = nextBlockPosition.getY();
                this.zoffset = nextBlockPosition.getZ();
            } else {
                p.normalize();
                p.mul(0.125f);
                this.xoffset += p.getX();
                this.yoffset += p.getY();
                this.zoffset += p.getZ();
            }
        } else {
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
            int newTickRate = (int) (((Math.max(1, harvest) / 6f) + (Hardness / 80f)) * 70f);
            tickGoal = (blockState.isAir(world, pos) || !blockState.getFluidState().isEmpty()) ? 1 : newTickRate;
            movingToNextBlock = false;
            nextBlockPosition = null;

        }
    }

    private void findNextBlock() {
        if(nextBlockPosition == null) {
            int xoff = (int)xoffset;
            int yoff = (int)yoffset;
            int zoff = (int)zoffset;
            boolean px = positiveX;
            boolean pz = positiveZ;
            BlockState blockState = world.getBlockState(quarryBlockRelative(new BlockPos(xoff, yoff, zoff)));
            while (((blockState.isAir(world, pos) || !blockState.getFluidState().isEmpty()) || !(blockState.getBlock() != BlockInit.QUARRYFRAME.get()) || blockState.getBlockHardness(world, pos) < 0) && this.ypos + yoff > 0) {
                if (xoff == Math.min(this.width - 1,(gridXoffset+1)*gridSize - 1) && px || xoff == Math.max(0,gridXoffset*gridSize) && !px) {
                    px = !px;
                    if (zoff == Math.min(this.length - 1,(gridZoffset+1)*gridSize - 1) && pz || zoff == Math.min(0,gridZoffset*gridSize) && !pz) {
                        pz = !pz;
                        yoff--;
                    } else {
                        zoff += pz ? 1 : -1;
                    }
                } else {
                    xoff += px ? 1 : -1;
                }

                blockState = world.getBlockState(quarryBlockRelative(new BlockPos(xoff, yoff, zoff)));
            }
            if (this.ypos + yoff < 3 && blockState.getBlockHardness(world, pos) < 0) {
                if((gridXoffset + 1)*gridSize-1 < this.width - 1) {
                    gridXoffset++;
                } else if((gridZoffset + 1)*gridSize-1 < this.length - 1){
                    gridXoffset = 0;
                    gridZoffset ++;
                } else {
                    this.finished = true;
                    this.running = false;
                    return;
                }
                nextBlockPosition = new BlockPos(gridXoffset*gridSize, 4, gridZoffset*gridSize);
                movingToNextBlock = true;
                tickGoal = 1;
                positiveX = px;
                positiveZ = pz;

            } else {
                nextBlockPosition = new BlockPos(xoff, yoff, zoff);
                movingToNextBlock = true;
                tickGoal = 1;
                positiveX = px;
                positiveZ = pz;
            }
        }
    }

    private boolean quarryRunnable() {
        assert world != null;
        if (world.getTileEntity(this.pos.add(0, 1, 0)) == null) { //check if there is a storage container above the quarry
            quarryStorage = null;
            return false;
        } else if (quarryStorage == null && world.getTileEntity(this.pos.add(0, 1, 0)) != null) { //if the quarry storage is null then set it
            quarryStorage = world.getTileEntity(this.pos.add(0, 1, 0));
            if (quarryInternalStorage.size() > 0 || finished) return false;
            return true;
        }
        if (quarryInternalStorage.size() > 0 || finished) return false;
        return true;
    }

    private boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir(world, pos) || blockState.getBlockHardness(world, pos) < 0) return false;
        else {
            IFluidState iFluidState = world.getFluidState(pos);
            if (dropBlock) {
                TileEntity tileEntity = blockState.hasTileEntity() ? world.getTileEntity(pos) : null;
                if (tileEntity != null) {
                    tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(tile -> {
                        int invSize = tile.getSlots();
                        for (int i = 0; i < invSize; i++) {
                            ItemStack prestack = tile.getStackInSlot(i);
                            ItemStack stack = new ItemStack(tile.getStackInSlot(i).getItem(), prestack.getCount());
                            if (stack.getCount() != 0) quarryInternalStorage.add(stack);
                            String n = stack.getItem().toString();
                            if(this.quarryBlocksMinned.containsKey(n)) {
                                this.quarryBlocksMinned.put(n, this.quarryBlocksMinned.get(n)+stack.getCount());
                            } else {
                                this.quarryBlocksMinned.put(n,1);
                            }
                            tile.getStackInSlot(i).setCount(0);
                        } //Add items in tile to quarry internal storage then break
                    });
                }
                if (!(world instanceof ServerWorld)) return false;
                Block.getDrops(blockState, (ServerWorld) world, pos, tileEntity, entity, ItemStack.EMPTY).forEach(drop -> { //get the drops for the block you have broken and loop through them
                    //add drops to hashmap
                    String n = drop.getItem().toString();

                    Boolean found = false;
                    for(int i = 0; i < inventory.getSlots(); i ++) {
                        if(drop.getItem().toString() == inventory.getStackInSlot(i).getItem().toString()) {
                            found =true;
                            break;
                        }
                    }

                    if(!found) {
                        if (this.quarryBlocksMinned.containsKey(n)) {
                            this.quarryBlocksMinned.put(n, this.quarryBlocksMinned.get(n) + drop.getCount());
                        } else {
                            this.quarryBlocksMinned.put(n, 1);
                        }
                        quarryStorage.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(it -> {
                            ItemStack te = ItemHandlerHelper.insertItem(it, new ItemStack(drop.getItem(), 1), false);
                            if (te != ItemStack.EMPTY) quarryInternalStorage.add(te);
                        });
                    }
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
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        CompoundNBT initValues = compound.getCompound("initvalues");
        if(initValues != null) {
            this.xpos = initValues.getFloat("xpos");
            this.ypos = initValues.getFloat("ypos");
            this.zpos = initValues.getFloat("zpos");
            this.xoffset = initValues.getFloat("xoff");
            this.yoffset = initValues.getFloat("yoff");
            this.zoffset = initValues.getFloat("zoff");
            this.tick = initValues.getInt("tick");
            this.running = initValues.getBoolean("running");
            this.positiveX = initValues.getBoolean("positiveX");
            this.positiveZ = initValues.getBoolean("positiveZ");
            this.width = initValues.getInt("Width");
            this.length = initValues.getInt("Length");
            this.xAligned = initValues.getBoolean("xAligned");
            this.facingPositon = initValues.getString("FacingPositon");
            this.initialised = initValues.getBoolean("initialised");
            this.running = initValues.getBoolean("running");
            this.finished = initValues.getBoolean("finished");
            this.gridSize = initValues.getInt("gridSize");
            this.gridXoffset = initValues.getInt("gridXOffset");
            this.gridZoffset = initValues.getInt("gridZOffset");
            this.previousBlockBreak = new BlockPos(initValues.getInt("previousX"), 0, initValues.getInt("previousZ"));
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        write(nbt);
        return nbt;
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

            for (int x = 0; x < this.width; x++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(x, 5, zoffset)), Blocks.AIR.getDefaultState());
            }
            for (int z = 0; z < this.length; z++) {
                world.setBlockState(quarryBlockRelative(new BlockPos(xoffset, 5, z)), Blocks.AIR.getDefaultState());
            }
            for (int y = (int)yoffset + 1; y < 5; y++) {
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

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Quarry Miner");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new QuarryContainer(i,playerInventory,this);
    }
}
