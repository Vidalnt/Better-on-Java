package com.xkingdark.bob.blocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TallEyeblossomBlock
    extends TallFlowerBlock
    implements Fertilizable
{

    public static final EnumProperty<DoubleBlockHalf> HALF =
        Properties.DOUBLE_BLOCK_HALF;
    private final EyeblossomState state;

    public TallEyeblossomBlock(EyeblossomState state, Settings settings) {
        super(settings);
        this.state = state;
        this.setDefaultState(
            this.stateManager.getDefaultState().with(
                HALF,
                DoubleBlockHalf.LOWER
            )
        );
    }

    @Override
    protected void appendProperties(
        StateManager.Builder<Block, BlockState> builder
    ) {
        builder.add(HALF);
    }

    @Override
    protected void randomTick(
        BlockState state,
        ServerWorld world,
        BlockPos pos,
        Random random
    ) {
        if (this.updateStateAndNotifyOthers(state, world, pos, random)) {
            world.playSound(
                null,
                pos,
                this.state.getOpposite().longSound,
                SoundCategory.BLOCKS,
                1.0F,
                1.0F
            );
        }

        super.randomTick(state, world, pos, random);
    }

    @Override
    protected void scheduledTick(
        BlockState state,
        ServerWorld world,
        BlockPos pos,
        Random random
    ) {
        if (this.updateStateAndNotifyOthers(state, world, pos, random)) {
            world.playSound(
                null,
                pos,
                this.state.getOpposite().sound,
                SoundCategory.BLOCKS,
                1.0F,
                1.0F
            );
        }

        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        DoubleBlockHalf half = state.get(HALF);
        if (half != DoubleBlockHalf.UPPER) {
            return super.canPlaceAt(state, world, pos);
        }

        BlockState blockState = world.getBlockState(pos.down());
        return (
            (blockState.isOf(Blocks.CLOSED_TALL_EYEBLOSSOM) ||
                blockState.isOf(Blocks.OPEN_TALL_EYEBLOSSOM)) &&
            blockState.get(HALF) == DoubleBlockHalf.LOWER
        );
    }

    private boolean updateStateAndNotifyOthers(
        BlockState state,
        ServerWorld world,
        BlockPos pos,
        Random random
    ) {
        if (
            !world.getRegistryKey().equals(World.OVERWORLD) ||
            world.isDay() != this.state.open
        ) return false;

        EyeblossomState eyeblossomState = this.state.getOpposite();
        BlockState blossomState = eyeblossomState.getBlockState();
        world.setBlockState(pos, blossomState, Block.NOTIFY_LISTENERS);

        DoubleBlockHalf half = state.get(HALF);
        BlockPos blockPos =
            half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        world.setBlockState(
            blockPos,
            blossomState.with(HALF, half.getOtherHalf()),
            Block.NOTIFY_ALL
        );

        world.emitGameEvent(
            GameEvent.BLOCK_CHANGE,
            pos,
            GameEvent.Emitter.of(state)
        );
        eyeblossomState.spawnTrailParticle(world, pos, random);

        BlockPos.iterate(pos.add(-3, -2, -3), pos.add(3, 2, 3)).forEach(
            otherPos -> {
                BlockState blockState = world.getBlockState(otherPos);
                if (blockState == state) {
                    double d = Math.sqrt(pos.getSquaredDistance(otherPos));
                    int i = random.nextBetween(
                        (int) (d * 5.0),
                        (int) (d * 10.0)
                    );

                    world.scheduleBlockTick(otherPos, state.getBlock(), i);
                }
            }
        );

        return true;
    }

    /*@Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        BlockPos blockPos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.isOf(this) && blockState.get(HALF) != half) {
            world.setBlockState(blockPos, net.minecraft.block.Blocks.AIR.getDefaultState(), 35);
            return super.onBreak(world, blockPos, blockState, player);
        }

        return super.onBreak(world, pos, state, player);
    }*/

    public enum EyeblossomState {
        OPEN(
            true,
            StatusEffects.BLINDNESS,
            11.0F,
            SoundEvents.BLOCK_EYEBLOSSOM_OPEN_LONG,
            SoundEvents.BLOCK_EYEBLOSSOM_OPEN,
            16545810
        ),
        CLOSED(
            false,
            StatusEffects.NAUSEA,
            7.0F,
            SoundEvents.BLOCK_EYEBLOSSOM_CLOSE_LONG,
            SoundEvents.BLOCK_EYEBLOSSOM_CLOSE,
            6250335
        );

        final boolean open;
        final RegistryEntry<StatusEffect> stewEffect;
        final float effectLengthInSeconds;
        final SoundEvent longSound;
        final SoundEvent sound;
        private final int particleColor;

        EyeblossomState(
            final boolean open,
            final RegistryEntry<StatusEffect> stewEffect,
            final float effectLengthInSeconds,
            final SoundEvent longSound,
            final SoundEvent sound,
            final int particleColor
        ) {
            this.open = open;
            this.stewEffect = stewEffect;
            this.effectLengthInSeconds = effectLengthInSeconds;
            this.longSound = longSound;
            this.sound = sound;
            this.particleColor = particleColor;
        }

        public Block getBlock() {
            return this.open
                ? Blocks.OPEN_TALL_EYEBLOSSOM
                : Blocks.CLOSED_TALL_EYEBLOSSOM;
        }

        public BlockState getBlockState() {
            return this.getBlock().getDefaultState();
        }

        public EyeblossomState getOpposite() {
            return of(!this.open);
        }

        public boolean isOpen() {
            return this.open;
        }

        public static EyeblossomState of(boolean open) {
            return open ? OPEN : CLOSED;
        }

        public void spawnTrailParticle(
            ServerWorld world,
            BlockPos pos,
            Random random
        ) {
            Vec3d vec3d = pos.toCenterPos();
            double d = 0.5 + random.nextDouble();
            Vec3d vec3d2 = new Vec3d(
                random.nextDouble() - 0.5,
                random.nextDouble() + 1.0,
                random.nextDouble() - 0.5
            );
            Vec3d vec3d3 = vec3d.add(vec3d2.multiply(d));
            TrailParticleEffect trailParticleEffect = new TrailParticleEffect(
                vec3d3,
                this.particleColor,
                (int) (20.0 * d)
            );
            world.spawnParticles(
                trailParticleEffect,
                vec3d.x,
                vec3d.y,
                vec3d.z,
                1,
                0.0,
                0.0,
                0.0,
                0.0
            );
        }

        public SoundEvent getLongSound() {
            return this.longSound;
        }
    }
}
