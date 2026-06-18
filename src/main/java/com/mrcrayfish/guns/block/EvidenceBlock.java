package com.mrcrayfish.guns.block;

import com.mrcrayfish.guns.tileentity.EvidenceTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/** kollisionsloser Beweis-Block */
public class EvidenceBlock extends Block
{
    // kleine sichtbare Box fürs Raytracing
    private static final VoxelShape SHAPE =
        Block.box(4, 0, 4, 12, 2, 12);

    public EvidenceBlock()
    {
        super(Block.Properties
            .of(Material.CLAY)
            .noCollission()
            .strength(0.0F));
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        IBlockReader world,
        BlockPos pos,
        ISelectionContext ctx)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
        BlockState state,
        IBlockReader world,
        BlockPos pos,
        ISelectionContext ctx)
    {
        return VoxelShapes.empty();
    }

    // keine Drops
    @Override
    public void playerDestroy(
        World world,
        PlayerEntity player,
        BlockPos pos,
        BlockState state,
        @Nullable TileEntity te,
        net.minecraft.item.ItemStack stack)
    {
        world.removeBlock(pos, false);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new EvidenceTileEntity();
    }
}
