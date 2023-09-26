package net.qf.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.qf.impl.util.EntityChecker;
import net.qf.impl.util.RaycastHelper;
import net.qf.impl.util.SkillUtilities;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.qf.api.SkillInfo.SkillMechanism.*;

/**
 * This defines Skill's infomation
 *
 * @param mechanism skill's mechanism. single target, or multi target, or summon projectile.
 * @param targetType skill's Activation parameter. it will be used different by the mechanism type. <p>
 *                  for example, if mechanism is <b>AOE</b>, <b>SELF</b> means center is the caster. and <b>TARGET</b> means player's raytrace position.
 * @param radius    skill's radius. this also will be used different by the mechanism type. <p>
 *                  for example, if mechanism is <b>PROJECTILE</b>, 0 means single target, over 0 means deals <b>AOE</b> damage on nearby entities.
 */
public record SkillInfo(SkillMechanism mechanism, TargetType targetType, float radius, float vertical, boolean debug) {

        public static Codec<SkillInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("mechanism", "ONE_TARGET").forGetter(o -> ((SkillInfo) o).mechanism().name()),
                        Codec.STRING.optionalFieldOf("targetType", "ALL").forGetter(o -> ((SkillInfo) o).targetType.name()),
                        Codec.FLOAT.optionalFieldOf("radius", 0F).forGetter(SkillInfo::radius),
                        Codec.FLOAT.optionalFieldOf("vertical", 0F).forGetter(SkillInfo::vertical),
                        Codec.BOOL.optionalFieldOf("debug", false).forGetter(SkillInfo::debug)
                ).apply(instance, (mechanism, targetType,  radius, vertical, debug) -> {
                    return new SkillInfo(SkillMechanism.valueOf(mechanism), TargetType.valueOf(targetType), radius, vertical, debug);
                })
        );

        public <T extends LivingEntity> Properties<T> getProperties(Class<T> target, LivingEntity caster) {
            return new Properties<>(target, caster, caster.getPos())
                    .vertical(vertical)
                    .radius(radius)
                    .mechanism(mechanism)
                    .type(targetType)
                    .debug(this.debug);

        }

        public enum SkillMechanism {
            AOE {
                @Override
                public <T extends LivingEntity> List<T> getTargets(Properties properties) {
                    double raycastDistance = properties.raycastDistance;
                    float vertical = properties.vertical;
                    float radius = properties.radius;

                    Box box = Box.of(properties.pos, radius, vertical, radius);

                    if (properties.debug) {
                        SkillUtilities.spawnDebugParticles(box, properties.world);
                    }

                    List<T> entrylist = properties.caster.getWorld().getEntitiesByClass(properties.expectType, box, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
                    return entrylist;
                }
            }, IN_FRONT {
                @Override
                public <T extends LivingEntity> List<T> getTargets(Properties properties) {
                    Vec3d location = SkillUtilities.getEyeView(properties.caster, properties.raycastDistance);

                    double minX = Math.min(properties.caster.getX(), location.x);
                    double minY = Math.min(properties.caster.getY(), location.y);
                    double minZ = Math.min(properties.caster.getZ(), location.z);

                    double maxX = Math.max(properties.caster.getX(), location.x);
                    double maxY = Math.max(properties.caster.getY(), location.y);
                    double maxZ = Math.max(properties.caster.getZ(), location.z);

                    Box box = new Box(minX - properties.radius, minY - properties.vertical, minZ - properties.radius
                            , maxX + properties.radius, maxY + properties.vertical, maxZ + properties.radius);

                    if (properties.debug) {
                        SkillUtilities.spawnDebugParticles(box, properties.world);
                    }

                    var list = properties.caster.getWorld().getEntitiesByClass(properties.expectType, box, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);

                    return list;
                }
            }, ONE_TARGET {
                @Override
                public <T extends LivingEntity> List<T> getTargets(Properties properties) {
                        EntityHitResult check = (EntityHitResult) RaycastHelper.raycast(properties.caster, 10F, 0, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
                        if (check.getEntity() != null) {
                            var entity = check.getEntity();
                            if (entity instanceof LivingEntity livingEntity) {
                                if (properties.expectType.isInstance(livingEntity)) {
                                    return ObjectList.of( (T)livingEntity);
                                }
                            }
                        }
                        return ObjectLists.emptyList();
                }
            },
            SELF {
                @Override
                public <T extends LivingEntity> List<T> getTargets(Properties properties) {
                    return (List<T>) List.of(properties.caster);
                }
            }
            ;

            public abstract <T extends LivingEntity> List<T> getTargets(Properties properties);
        }

        public enum TargetType {
            ALLIES {
                @Override
                public boolean isIncludes(LivingEntity caster, LivingEntity target) {
                    return !ENEMIES.isIncludes(caster, target);
                }
            },
            ENEMIES {
                @Override
                public boolean isIncludes(LivingEntity caster, LivingEntity target) {
                    if (caster instanceof PlayerEntity player) {
                        if (EntityChecker.isTamed(target)) {
                            return false;
                        }
                        if (target instanceof PlayerEntity targetPlayer) {
                            if (target == caster) {
                                return false;
                            }
                            return targetPlayer.isTeammate(caster);
                        }
                    }
                     else {
                        return target instanceof PlayerEntity;
                    }
                    return true;
                }

                @Override
                public boolean includesCaster() {
                    return false;
                }
            },
            ALL {
                @Override
                public <T extends LivingEntity> List<T> getMatchingEntities(List<T> list, LivingEntity caster) {
                    return list;
                }

                @Override
                public boolean isIncludes(LivingEntity caster, LivingEntity target) {
                    return true;
                }
            };

            public <T extends LivingEntity> List<T> getMatchingEntities(List<T> list, LivingEntity caster) {
                return list.stream()
                        .filter(target -> this.isIncludes(caster, target))
                        .collect(Collectors.toList());
            };
            public abstract boolean isIncludes(LivingEntity caster, LivingEntity target);
            public boolean includesCaster() {
                return true;
            }
        }

        public static class Properties<T extends LivingEntity> {
            private final Class<T> expectType;
            private SkillMechanism mechanism = AOE;
            private TargetType type = TargetType.ALL;
            private final LivingEntity caster;
            private final World world;
            private final Vec3d pos;
            private float radius = 1;
            private float vertical = 1;
            private boolean forceExcludeCaster = false;
            private boolean debug = false;

            private double raycastDistance = 10;

            public Properties(@NotNull Class<T> expectType, @NotNull LivingEntity caster, @NotNull Vec3d pos) {
                this.expectType = expectType;
                this.caster = caster;
                this.world = caster.getWorld();
                this.pos = pos;
            }

            public List<T> search() {
                List<T> list = this.mechanism.getTargets(this);

                list.removeIf(Objects::isNull);
                var filteredList = this.type.getMatchingEntities(list, this.caster);

                if (this.forceExcludeCaster || !this.type.includesCaster()) {
                    filteredList.removeIf(x -> x == this.caster);
                }

                filteredList.removeIf(x -> !x.isAlive());

                return filteredList;
            }

            public Properties<T> radius(float radius) {
                this.radius = radius;
                return this;
            }

            public Properties<T> type(TargetType type) {
                this.type = type;
                return this;
            }

            public Properties<T> vertical(float vertical) {
                this.vertical = vertical;
                return this;
            }

            public Properties<T> forceExcludeCaster(boolean bool) {
                this.forceExcludeCaster = bool;
                return this;
            }

            public Properties<T> debug(boolean bool) {
                this.debug = bool;
                return this;
            }

            public Properties<T> mechanism(SkillMechanism mechanism) {
                this.mechanism = mechanism;
                return this;
            }

            public Properties<T> raycastDistance(double d) {
                this.raycastDistance = d;
                return this;
            }
        }
}
