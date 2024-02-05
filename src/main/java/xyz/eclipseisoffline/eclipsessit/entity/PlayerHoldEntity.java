package xyz.eclipseisoffline.eclipsessit.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class PlayerHoldEntity extends AreaEffectCloudEntity {

    public PlayerHoldEntity(World world, ServerPlayerEntity player) {
        super(world, player.getX(), player.getY(), player.getZ());
        setInvisible(true);
        setRadius(0);
    }

    @Override
    public void tick() {}
}
