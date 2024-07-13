package com.jackd.exchickens.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import com.jackd.exchickens.entity.EntityExplodingChicken;

import java.util.List;

public class BlockChickenTrap extends HorizontalFacingBlock {

    public static final MapCodec<BlockChickenTrap> CODEC = createCodec(BlockChickenTrap::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    private static final VoxelShape SHAPE_Z;
    private static final VoxelShape SHAPE_X;

    public BlockChickenTrap(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if(state.get(ACTIVE) && world.isReceivingRedstonePower(pos)) {
            // when powered, find the chicken trapped by the block and explode it
            List<EntityExplodingChicken> chickens = world.getEntitiesByClass(EntityExplodingChicken.class, Box.enclosing(pos, pos), EntityPredicates.VALID_ENTITY);
            if(chickens != null && chickens.size() > 0) {
                for(EntityExplodingChicken chicken : chickens) {
                    if(chicken.isTrappedAt(pos)) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        chicken.explode();
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        // don't trigger if on client or already armed
        if(world.isClient || state.get(ACTIVE)) return;
        // check if an exploding chicken is on the plate
        if(entity instanceof EntityExplodingChicken chicken && !chicken.isTamed()) {
            world.setBlockState(pos, state.with(ACTIVE, true));
            chicken.setTrappedBlockPos(pos);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, ACTIVE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)) {
            case NORTH:
            case SOUTH:
                return SHAPE_Z;
            case EAST:
            case WEST:
            default:
                return SHAPE_X;
        }
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    static {
        VoxelShape base = Block.createCuboidShape(0d, 0d, 0d, 16d, 2d, 16d);
        VoxelShape armsZ = Block.createCuboidShape(0d, 2d, 7d, 16d, 14d, 9d);
        VoxelShape armsX = Block.createCuboidShape(7d, 2d, 0d, 9d, 14d, 16d);
        SHAPE_Z = VoxelShapes.combine(base, armsZ, BooleanBiFunction.OR);
        SHAPE_X = VoxelShapes.combine(base, armsX, BooleanBiFunction.OR);
    }

}
