package net.qf.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.qf.api.ESekaiSchool;
import net.qf.impl.ESekaiStatEntity;
import net.qf.impl.stat.ESekaiAttributeStat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Unique
    private ServerPlayerEntity getPlayer() {
        return (ServerPlayerEntity) (Object) this;
    }
}