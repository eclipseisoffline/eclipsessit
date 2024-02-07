package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;

public class LayPose extends PosingNPCPose {
    private final Direction direction;
    private byte lastHeadYaw;

    public LayPose(ServerPlayerEntity player) {
        super(player);
        direction = player.getHorizontalFacing();

        posingNPC.setPose(EntityPose.SLEEPING);

        BlockPos bedPos = player.getBlockPos().withY(player.getWorld().getBottomY());
        posingNPC.setSleepingPosition(bedPos);
        posingNPC.setPosition(posingNPC.getPos().withAxis(Axis.Y, player.getY()));
        BlockState bedBlock = Blocks.WHITE_BED.getDefaultState().with(Properties.HORIZONTAL_FACING,
                player.getHorizontalFacing().getOpposite()).with(Properties.BED_PART, BedPart.HEAD);

        spawnPackets.add(new BlockUpdateS2CPacket(bedPos, bedBlock));
        spawnPackets.add(new EntityPositionS2CPacket(posingNPC));
        despawnPackets.add(new BlockUpdateS2CPacket(player.getWorld(), bedPos));
    }

    @Override
    public void tick() {
        super.tick();
        byte headYaw = (byte) MathHelper.floor(getHeadYaw() * 256.0f / 360.0f);
        if (Math.abs(headYaw - lastHeadYaw) >= 1) {
            sendPacketToAwarePlayers(new EntitySetHeadYawS2CPacket(posingNPC, headYaw));
            this.lastHeadYaw = headYaw;
        }
    }

    private float getHeadYaw() {
        float headYaw = player.getHeadYaw();

        if (direction == Direction.WEST) {
            headYaw -= 90;
        } else if (direction == Direction.EAST) {
            headYaw += 90;
        } else if (direction == Direction.NORTH) {
            headYaw -= 180;
        }

        if (headYaw >= 315) {
            return headYaw - 360;
        } else if (headYaw <= 45) {
            return headYaw;
        } else if (headYaw >= 180) {
            return -45;
        }
        return 45;
    }
}
