package net.qf.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.qf.impl.QfDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {


    @Unique
    private ServerPlayerEntity getPlayer() {
        return (ServerPlayerEntity) (Object) this;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void esekai$damageCheck(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source instanceof QfDamageSource)) {
            return;
        }

    }
}