package xyz.eclipseisoffline.eclipsessit.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.eclipsessit.EclipsesSit;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    public void stopPosing(CallbackInfo callbackInfo) {
        if (EclipsesSit.POSE_MANAGER.isPosing((ServerPlayerEntity) (Object) this)) {
            EclipsesSit.POSE_MANAGER.stopPosing((ServerPlayerEntity) (Object) this);
            callbackInfo.cancel();
        }
    }
}
