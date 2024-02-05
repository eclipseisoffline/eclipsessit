package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.server.network.ServerPlayerEntity;

public class LayPose extends SitPose {

    public LayPose(ServerPlayerEntity player) {
        super(player);
        // TODO fake posing player code
    }

    @Override
    public void startPosing() {
        super.startPosing();
        player.setInvisible(true);
    }

    @Override
    public void stopPosing() {
        super.stopPosing();
        player.setInvisible(false);
    }
}
