package com.jackd.exchickens.util;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;

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

}
