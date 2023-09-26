package net.qf.impl.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public class RaycastHelper {
    public static HitResult raycast(Entity entity, double maxDistance, float tickDelta, Predicate<Entity> predicate) {
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = entity.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(maxDistance)).expand(1.0D, 1.0D, 1.0D);

        return ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, maxDistance);
    }
}
