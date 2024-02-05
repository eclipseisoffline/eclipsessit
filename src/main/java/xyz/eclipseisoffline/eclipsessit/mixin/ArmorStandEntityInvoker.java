package xyz.eclipseisoffline.eclipsessit.mixin;

import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityInvoker {

    @Invoker
    void callSetSmall(boolean small);
}
