package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.eclipseisoffline.eclipsessit.entity.PlayerHoldEntity;

public class PlayerSitPose extends Pose {
    private final ServerPlayerEntity vehicle;

    public PlayerSitPose(ServerPlayerEntity driver, ServerPlayerEntity vehicle) {
        super(driver);
        this.vehicle = vehicle;
    }

    @Override
    public void startPosing() {
        PlayerHoldEntity first = new PlayerHoldEntity(vehicle.getWorld(), vehicle);
        PlayerHoldEntity second = new PlayerHoldEntity(vehicle.getWorld(), vehicle);

        player.getWorld().spawnEntity(first);
        player.getWorld().spawnEntity(second);

        first.startRiding(vehicle);
        second.startRiding(first);
        player.startRiding(second);

        vehicle.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(vehicle));
    }

    @Override
    public void stopPosing() {
        PlayerHoldEntity second = (PlayerHoldEntity) player.getVehicle();

        assert second != null;
        PlayerHoldEntity first = (PlayerHoldEntity) second.getVehicle();

        assert first != null;
        second.kill();
        first.kill();
    }

    @Override
    public boolean canPose() {
        return true;
    }
}
