package xyz.eclipseisoffline.eclipsessit;

import java.util.function.Function;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.eclipseisoffline.eclipsessit.poses.CrawlPose;
import xyz.eclipseisoffline.eclipsessit.poses.LayPose;
import xyz.eclipseisoffline.eclipsessit.poses.Pose;
import xyz.eclipseisoffline.eclipsessit.poses.PoseManager;
import xyz.eclipseisoffline.eclipsessit.poses.SitPose;

public class EclipsesSit implements ModInitializer {
    public static final Logger MOD_LOGGER = LoggerFactory.getLogger("EclipsesSit");
    public static final PoseManager POSE_MANAGER = new PoseManager();

    @Override
    public void onInitialize() {
        MOD_LOGGER.info("Eclipse's Sit mod initialising");

        registerPoseCommand("sit", (serverCommandSource -> new SitPose(serverCommandSource.getPlayer())));
        registerPoseCommand("crawl", (serverCommandSource -> new CrawlPose(serverCommandSource.getPlayer())));

        ServerTickEvents.END_SERVER_TICK.register((server) -> POSE_MANAGER.tickPosingPlayers());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> POSE_MANAGER.stopAllPoses());
    }

    private void registerPoseCommand(String commandName, Function<ServerCommandSource, Pose> poseCreator) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal(commandName).executes(context -> {
                    if (!context.getSource().isExecutedByPlayer()) {
                        return 1;
                    }

                    boolean success = POSE_MANAGER.startPosing(context.getSource().getPlayer(), poseCreator.apply(context.getSource()));

                    if (!success) {
                        context.getSource().sendError(Text.of("You can't pose right now!"));
                    }
                    return success ? 0 : 1;
                })
        )));
    }
}