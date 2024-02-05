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
        holdEntity.setPosition(holdEntity.getPos().offset(Direction.DOWN, 0.5));

        player.getWorld().spawnEntity(holdEntity);
        player.startRiding(holdEntity, true);
    }

    @Override
    public void stopPosing() {
        PlayerHoldEntity holdEntity = (PlayerHoldEntity) player.getVehicle();

        assert holdEntity != null;
        holdEntity.kill();
        player.setPosition(player.getPos().offset(Direction.UP, 0.5));
    }
}
