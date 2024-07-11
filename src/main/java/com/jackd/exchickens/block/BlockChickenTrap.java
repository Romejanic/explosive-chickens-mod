package com.jackd.exchickens.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

import com.jackd.exchickens.util.ExplosionSizes;

public class BlockChickenTrap extends HorizontalFacingBlock {

    public static final MapCodec<BlockChickenTrap> CODEC = createCodec(BlockChickenTrap::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public BlockChickenTrap(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if(state.get(ACTIVE) && world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            this.explode(world, pos);
        }
    }

    private void explode(World world, BlockPos pos) {
        if(world.isClient) return;
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), ExplosionSizes.chickenExplosion(), ExplosionSourceType.BLOCK);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HorizontalFacingBlock.FACING, ACTIVE);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

}
