package com.jackd.exchickens.util;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.math.BlockPos;

public class NbtUtils {
    
    public static void writeVector3f(NbtCompound nbt, String key, Vector3fc value) {
        NbtCompound out = new NbtCompound();
        out.putFloat("x", value.x());
        out.putFloat("y", value.y());
        out.putFloat("z", value.z());
        nbt.put(key, out);
    }

    @Nullable
    public static Vector3f readVector3f(NbtCompound nbt, String key) {
        if(!nbt.contains(key)) return null;
        NbtCompound data = nbt.getCompound(key);
        return new Vector3f(
            data.getFloat("x"), data.getFloat("y"), data.getFloat("z")
        );
    }

    public static UUID readUUID(NbtCompound nbt, Entity entity, String key) {
        if(!nbt.contains(key)) return null;
        if(nbt.containsUuid(key)) return nbt.getUuid(key);
        String value = nbt.getString(key);
        try {
            // try to parse UUID
            return UUID.fromString(value);
        } catch(IllegalArgumentException e) {
            // try to resolve name
            return ServerConfigHandler.getPlayerUuidByName(entity.getServer(), value);
        }
    }

    public static void writeBlockPos(NbtCompound nbt, String key, BlockPos pos) {
        nbt.putIntArray(key, new int[] {
            pos.getX(), pos.getY(), pos.getZ()
        });
    }

    public static Optional<BlockPos> readBlockPos(NbtCompound nbt, String key) {
        if(!nbt.contains(key, NbtElement.INT_ARRAY_TYPE)) return Optional.empty();
        int[] arr = nbt.getIntArray(key);
        return arr.length == 3 ? Optional.of(new BlockPos(arr[0], arr[1], arr[2])) : Optional.empty();
    }

}
