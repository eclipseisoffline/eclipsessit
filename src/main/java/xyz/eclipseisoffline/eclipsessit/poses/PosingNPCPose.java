package xyz.eclipseisoffline.eclipsessit.poses;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

public abstract class PosingNPCPose extends SitPose {
    private static final int NPC_AWARE_RANGE = 128;
    private static final List<Pair<EquipmentSlot, ItemStack>> CLEAR_EQUIPMENT_PACKET = new ArrayList<>();

    static {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            CLEAR_EQUIPMENT_PACKET.add(new Pair<>(equipmentSlot, ItemStack.EMPTY));
        }
    }

    protected final FakePlayer posingNPC;
    protected final List<Packet<?>> spawnPackets = new ArrayList<>();
    protected final List<Packet<?>> despawnPackets = new ArrayList<>();
    private final List<ServerPlayerEntity> npcAware = new ArrayList<>();

    private final PlayerListS2CPacket infoAddPacket;
    private final EntityEquipmentUpdateS2CPacket clearPlayerEquipmentPacket;
    private final EntitiesDestroyS2CPacket despawnPacket;
    private final PlayerRemoveS2CPacket infoRemovePacket;

    public PosingNPCPose(ServerPlayerEntity player) {
        super(player);

        GameProfile posingNPCProfile = new GameProfile(UUID.randomUUID(), getNPCName());
        posingNPCProfile.getProperties().putAll(player.getGameProfile().getProperties());

        posingNPC = FakePlayer.get(player.getServerWorld(), posingNPCProfile);

        posingNPC.setPosition(player.getPos());
        posingNPC.setYaw(player.getYaw());
        posingNPC.setPitch(player.getPitch());

        infoAddPacket = new PlayerListS2CPacket(EnumSet.of(Action.ADD_PLAYER, Action.INITIALIZE_CHAT, Action.UPDATE_GAME_MODE, Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME), Collections.singleton(posingNPC));
        clearPlayerEquipmentPacket = new EntityEquipmentUpdateS2CPacket(player.getId(), CLEAR_EQUIPMENT_PACKET);
        despawnPacket = new EntitiesDestroyS2CPacket(posingNPC.getId());
        infoRemovePacket = new PlayerRemoveS2CPacket(List.of(posingNPC.getUuid()));
    }

    @Override
    public void startPosing() {
        super.startPosing();
        player.setInvisible(true);

        updateAwarePlayers();
    }

    @Override
    public void tick() {
        super.tick();
        updateAwarePlayers();
    }

    @Override
    public void stopPosing() {
        super.stopPosing();
        player.setInvisible(false);

        despawnNPC();
        posingNPC.discard();
    }

    protected void sendPacketToAwarePlayers(Packet<?> packet) {
        for (ServerPlayerEntity isAware : npcAware) {
            isAware.networkHandler.sendPacket(packet);
        }
    }

    private void updateAwarePlayers() {
        List<ServerPlayerEntity> shouldBeAwarePlayers = getShouldBeAware();

        List<ServerPlayerEntity> areAware = new ArrayList<>(npcAware);
        for (ServerPlayerEntity isAware : areAware) {
            if (isAware.isDisconnected()) {
                npcAware.remove(isAware);
            } else if (!shouldBeAwarePlayers.contains(isAware)) {
                npcAware.remove(isAware);
                for (Packet<?> despawnPacket : despawnPackets) {
                    isAware.networkHandler.sendPacket(despawnPacket);
                }
                isAware.networkHandler.sendPacket(despawnPacket);
                isAware.networkHandler.sendPacket(infoRemovePacket);
            }
        }

        for (ServerPlayerEntity shouldBeAware : shouldBeAwarePlayers) {
            if (npcAware.contains(shouldBeAware)) {
                continue;
            }
            shouldBeAware.networkHandler.sendPacket(infoAddPacket);
            shouldBeAware.networkHandler.sendPacket(new EntitySpawnS2CPacket(posingNPC));
            shouldBeAware.networkHandler.sendPacket(clearPlayerEquipmentPacket);
            shouldBeAware.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(posingNPC.getId(), posingNPC.getDataTracker().getChangedEntries()));
            for (Packet<?> spawnPacket : spawnPackets) {
                shouldBeAware.networkHandler.sendPacket(spawnPacket);
            }

            npcAware.add(shouldBeAware);
        }

        if (posingNPC.getDataTracker().isDirty()) {
            sendPacketToAwarePlayers(new EntityTrackerUpdateS2CPacket(posingNPC.getId(), posingNPC.getDataTracker().getDirtyEntries()));
        }
    }

    private void despawnNPC() {
        for (Packet<?> despawnPacket : despawnPackets) {
            sendPacketToAwarePlayers(despawnPacket);
        }
        sendPacketToAwarePlayers(despawnPacket);
        sendPacketToAwarePlayers(infoRemovePacket);
    }

    private List<ServerPlayerEntity> getShouldBeAware() {
        Box range = new Box(player.getPos().add(-NPC_AWARE_RANGE, -NPC_AWARE_RANGE, -NPC_AWARE_RANGE),
                player.getPos().add(NPC_AWARE_RANGE, NPC_AWARE_RANGE, NPC_AWARE_RANGE));
        return player.getServerWorld().getPlayers((playerInWorld) -> range.contains(playerInWorld.getPos()));
    }

    protected String getNPCName() {
        return player.getGameProfile().getName();
    }
}
