package com.pyrodeathadder.potionberries.objects.blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.stream.Stream;

public class QuarryFrame extends Block {
    public static final IntegerProperty SECTION = IntegerProperty.create("section",0,4);

    private static final VoxelShape SHAPE_CONNECTOR = Stream.of(
            Block.makeCuboidShape(4, 4, 4, 12, 12, 12),
            Block.makeCuboidShape(6, 6, 0, 10, 10, 4),
            Block.makeCuboidShape(6, 6, 12, 10, 10, 16),
            Block.makeCuboidShape(6, 0, 6, 10, 4, 10),
            Block.makeCuboidShape(0, 6, 6, 4, 10, 10),
            Block.makeCuboidShape(12, 6, 6, 16, 10, 10)
    ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_PIPE_EW = Stream.of(
            Block.makeCuboidShape(0, 6, 6, 16, 10, 10))
            .reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_PIPE_NS = Stream.of(
            Block.makeCuboidShape(6, 6, 0, 10, 10, 16))
            .reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_ARM = Stream.of(
            Block.makeCuboidShape(6, 0, 6, 10, 16, 10))
            .reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    private static final VoxelShape SHAPE_DRILL = Stream.of(
            Block.makeCuboidShape(6, 3, 6, 10, 16, 10),
            Block.makeCuboidShape(7, 0, 7, 9, 3, 9))
            .reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);}).get();

    public QuarryFrame(Block.Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(SECTION, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context){
        return this.getDefaultState().with(SECTION, 0);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SECTION);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(SECTION)) {
            case 0: return SHAPE_PIPE_NS;
            case 1: return SHAPE_PIPE_EW;
            case 2: return SHAPE_CONNECTOR;
            case 3: return SHAPE_ARM;
            case 4: return SHAPE_DRILL;
        }
        return SHAPE_CONNECTOR;
    }
}
