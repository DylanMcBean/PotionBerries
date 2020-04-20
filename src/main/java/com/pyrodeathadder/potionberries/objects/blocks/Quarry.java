package com.pyrodeathadder.potionberries.objects.blocks;

import com.pyrodeathadder.potionberries.init.ModTileEntityType;
import com.pyrodeathadder.potionberries.tileentity.QuarryTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

public class Quarry extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture",0,8);
    QuarryTileEntity quarryTileEntity;

    public Quarry(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TEXTURE,0));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(TEXTURE);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        quarryTileEntity = ModTileEntityType.QUARRY.get().create();
        return quarryTileEntity;
    }

    @Override
    public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        super.onBlockPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        quarryTileEntity.removeStructure();
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof QuarryTileEntity) {
                quarryTileEntity.removeStructure();
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            if (!player.isShiftKeyDown()) {
                final TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof QuarryTileEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (QuarryTileEntity) tile, pos);
                }
            }else if (player.isShiftKeyDown() && player.getHeldItemMainhand() == ItemStack.EMPTY) {
                sortByValue(quarryTileEntity.quarryBlocksMinned);
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Items Collected:"));
                for (Map.Entry entry : quarryTileEntity.quarryBlocksMinned.entrySet()) {
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent("    " + entry.getKey().toString() + ": " + entry.getValue().toString()));
                    //worldIn.getServer().getPlayerList().sendMessage(new TranslationTextComponent(entry.getKey().toString() + ": " + entry.getValue().toString()));
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
