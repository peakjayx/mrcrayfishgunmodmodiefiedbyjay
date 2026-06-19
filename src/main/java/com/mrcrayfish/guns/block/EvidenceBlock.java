package com.mrcrayfish.guns.block;

import com.mrcrayfish.guns.tileentity.EvidenceTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/** kollisionsloser Beweis-Block */
public class EvidenceBlock extends Block implements IWaterLoggable
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 2, 12);

    public EvidenceBlock()
    {
        super(Block.Properties.of(Material.CLAY).noCollission().strength(0.0F));
        this.registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED)
            ? Fluids.WATER.getSource(false)
            : super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
    {
        return !state.getValue(WATERLOGGED) && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState)
    {
        if(!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER)
        {
            if(!world.isClientSide())
            {
                world.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
                world.getLiquidTicks().scheduleTick(
                    pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
            }
            return true;
        }
        return false;
    }

    @Override
    public VoxelShape getShape(
        BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
        BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return VoxelShapes.empty();
    }

    @Override
    public void playerDestroy(
        World world, PlayerEntity player, BlockPos pos,
        BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        world.removeBlock(pos, false);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new EvidenceTileEntity();
    }
}
