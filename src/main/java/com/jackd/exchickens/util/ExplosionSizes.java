package com.jackd.exchickens.util;

public class ExplosionSizes {
    
    public static final float ITEM_EXPLOSION = 5.0f;

    public static final float MIN_CHICKEN_EXPLODE_RANGE = 5.0F;
    public static final float MAX_CHICKEN_EXPLODE_RANGE = 12.0F;

    public static final float MIN_LAUNCHER_EXPLODE_RANGE = 2.0F;
    public static final float MAX_LAUNCHER_EXPLODE_RANGE = 4.0F;

    public static float chickenExplosion() {
        return randomSize(MIN_CHICKEN_EXPLODE_RANGE, MAX_CHICKEN_EXPLODE_RANGE);
    }

    public static float launcherExplosion() {
        return randomSize(MIN_LAUNCHER_EXPLODE_RANGE, MAX_LAUNCHER_EXPLODE_RANGE);
    }

    private static float randomSize(float min, float max) {
        return min + (max - min) * MathUtils.RANDOM.nextFloat();
    }

}
