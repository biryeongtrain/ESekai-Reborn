package net.qf.impl.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkillUtilities {
    public static Vec3d getEyeView(LivingEntity entity, double distance) {
        return entity.getEyePos()
                .add(entity.getRotationVector()
                        .multiply(distance));
    }

    public static void spawnDebugParticles(Box box, World world) {
        if (world.isClient) {
            return;
        }

        for (double x = box.minX; x < box.maxX; x += 0.3F) {
            for (double y = box.minY; y < box.maxY; y += 1F) {
                for (double z = box.minZ; z < box.maxZ; z+= 0.3F) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0 , 0, 0, 0, 0);
                }
            }
        }
    }
}
