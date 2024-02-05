package xyz.eclipseisoffline.eclipsessit.poses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.server.network.ServerPlayerEntity;

public class PoseManager {

    private final Map<ServerPlayerEntity, Pose> posingPlayers = new HashMap<>();

    public boolean startPosing(ServerPlayerEntity player, Pose pose) {
        if (posingPlayers.containsKey(player) || !pose.canPose()) {
            return false;
        }

        posingPlayers.put(player, pose);
        pose.startPosing();
        return true;
    }

    public void tickPosingPlayers() {
        // Copy set to avoid issues with removing from the set while looping through it (when stopping posing)
        Set<Entry<ServerPlayerEntity, Pose>> posingPlayersSet = new HashSet<>(posingPlayers.entrySet());
        for (Entry<ServerPlayerEntity, Pose> posingPlayer : posingPlayersSet) {
            if (posingPlayer.getKey().isSneaking()) {
                stopPosing(posingPlayer.getKey());
            } else {
                posingPlayer.getValue().tick();
            }
        }
    }

    public boolean isPosing(ServerPlayerEntity player) {
        return posingPlayers.containsKey(player);
    }

    public void stopPosing(ServerPlayerEntity player) {
        if (!posingPlayers.containsKey(player)) {
            return;
        }

        Pose pose = posingPlayers.get(player);
        posingPlayers.remove(player);
        pose.stopPosing();
    }

    public void stopAllPoses() {
        Set<Entry<ServerPlayerEntity, Pose>> posingPlayersSet = new HashSet<>(posingPlayers.entrySet());
        for (Entry<ServerPlayerEntity, Pose> posingPlayer : posingPlayersSet) {
            stopPosing(posingPlayer.getKey());
        }
    }
}
