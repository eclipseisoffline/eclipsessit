package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class CrawlPose extends Pose {
    private BlockPos previousUpBlockPos = null;

    public CrawlPose(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public void tick() {
        BlockPos upBlockPos = player.getBlockPos().up();
        if (upBlockPos.equals(previousUpBlockPos)) {
            return;
        }

        if (previousUpBlockPos != null) {
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(player.getWorld(), previousUpBlockPos));
        }

        player.networkHandler.sendPacket(new BlockUpdateS2CPacket(upBlockPos, Blocks.BARRIER.getDefaultState()));
        previousUpBlockPos = upBlockPos;
    }

    @Override
    public void stopPosing() {
        if (previousUpBlockPos != null) {
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(player.getWorld(), previousUpBlockPos));
        }
    }
}
