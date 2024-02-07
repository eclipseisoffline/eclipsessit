package xyz.eclipseisoffline.eclipsessit.poses;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class FlipPose extends PosingNPCPose {

    public FlipPose(ServerPlayerEntity player) {
        super(player);
        System.out.println("Dinnerbone".equals(Formatting.strip(posingNPC.getName().getString())));
    }

    @Override
    protected String getNPCName() {
        return "Dinnerbone";
    }
}
