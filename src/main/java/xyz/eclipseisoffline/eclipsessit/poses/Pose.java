package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class Pose {
    protected final ServerPlayerEntity player;

    protected Pose(ServerPlayerEntity player) {
        this.player = player;
    }

    public void startPosing() {}
    public void tick() {}
    public void stopPosing() {}

    public boolean canPose() {
        return player.isOnGround() && !player.hasVehicle() && !player.hasPassengers() && !player.isSneaking() && !player.isSleeping();
    }
}
