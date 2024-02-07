package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import xyz.eclipseisoffline.eclipsessit.entity.PlayerHoldEntity;

public class SitPose extends Pose {

    public SitPose(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public void startPosing() {
        PlayerHoldEntity holdEntity = new PlayerHoldEntity(player.getWorld(), player);
        holdEntity.setPosition(holdEntity.getPos().offset(Direction.DOWN, 0.4));

        player.getWorld().spawnEntity(holdEntity);
        player.startRiding(holdEntity, true);
    }

    @Override
    public void stopPosing() {
        try {
            PlayerHoldEntity holdEntity = (PlayerHoldEntity) player.getVehicle();

            if (holdEntity != null) {
                holdEntity.discard();
            }
        } catch (ClassCastException ignored) {}
        player.setPosition(player.getPos().offset(Direction.UP, 0.4));
    }
}
