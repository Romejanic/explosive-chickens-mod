package com.jackd.exchickens.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.StateManager.Builder;

public class BlockChickenTrap extends HorizontalFacingBlock {

    public static final MapCodec<BlockChickenTrap> CODEC = createCodec(BlockChickenTrap::new);

    public BlockChickenTrap(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HorizontalFacingBlock.FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

}
