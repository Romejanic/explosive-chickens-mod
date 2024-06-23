package com.jackd.exchickens.util;

import org.joml.Vector3f;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class MathUtils {

    public static final Random RANDOM = Random.create();
    
    public static final Vector3f ZERO = new Vector3f();

    public static final Vector3f randomLaunchDirection(LivingEntity entity) {
        float angle = entity.headYaw;
        float x = -MathHelper.cos(angle * MathHelper.DEGREES_PER_RADIAN);
        float z = MathHelper.sin(angle * MathHelper.DEGREES_PER_RADIAN);
        float y = MathHelper.nextFloat(RANDOM, 0.5f, 4f);
        return new Vector3f(x, y, z).normalize();
    }

    public static final float xzAngle(Vector3f v) {
        return (float)MathHelper.atan2(v.z, v.x) / MathHelper.DEGREES_PER_RADIAN;
    }

    public static final Vector3f addJitter(Vector3f v, float amount, float divergence) {
        Vector3f rand = new Vector3f(
            MathHelper.nextFloat(RANDOM, -1f, 1f),
            MathHelper.nextFloat(RANDOM, -1f, 1f),
            MathHelper.nextFloat(RANDOM, -1f, 1f)
        ).mul(amount);
        float t = MathHelper.nextFloat(RANDOM, 0f, divergence);
        return v.lerp(rand, t).normalize();
    }

    public static final Vec3d dirToVelocity(Vector3f dir, double mag) {
        return new Vec3d(dir).multiply(mag);
    }

}
