package dev.mayuna.topggmodule.managers;

import dev.mayuna.modularbot.ModularBot;
import dev.mayuna.modularbot.concurrent.ModularTimer;
import dev.mayuna.modularbot.logging.MayuLogger;
import dev.mayuna.topggmodule.TopGGModule;
import dev.mayuna.topggmodule.util.Config;
import dev.mayuna.topggsdk.api.TopGGAPIResponse;
import lombok.Getter;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class TopGGManager {

    private @Getter ModularTimer updateTimer;

    /**
     * Starts Top.GG Manager
     */
    public void start() {
        TopGGModule.getLog().info("Loading Top.gg Manager...");

        startSchedulers();
    }

    /**
     * Starts schedulers within Modular Discord Bot framework
     */
    private void startSchedulers() {
        TopGGModule.getLog().info("Starting up update timer...");
        updateTimer = TopGGModule.getInstance().getScheduler().createTimer();

        Config.TopGG.Updates updatesConfig = TopGGModule.getConfig().getTopgg().getUpdates();

        updateTimer.getInstance().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTopGGStats();
            }
        }, updatesConfig.getStartupDelayMillis(), updatesConfig.getUpdateIntervalMillis());
    }

    private void updateTopGGStats() {
        MayuLogger logger = TopGGModule.getLog();
        Config.TopGG.Updates updatesConfig = TopGGModule.getConfig().getTopgg().getUpdates();

        ShardManager shardManager = ModularBot.getWrappedShardManager().getInstance();

        int serverCount = (int) Math.min(shardManager.getGuildCache().size(), Integer.MAX_VALUE);
        Integer shardCount = null;

        if (updatesConfig.isUpdateShardCount()) {
            shardCount = shardManager.getShardsTotal();
        }

        logger.info(getUpdateLogMessage(serverCount, shardCount));

        sendUpdateBotStatsRequest(serverCount, shardCount);
    }

    private void sendUpdateBotStatsRequest(int serverCount, Integer shardCount) {
        CompletableFuture<TopGGAPIResponse> completableFuture;

        if (shardCount != null) {
            completableFuture = TopGGModule.getTopGGAPI().updateBotStats(serverCount, shardCount).execute();
        } else {
            completableFuture = TopGGModule.getTopGGAPI().updateBotStats(serverCount).execute();
        }

        completableFuture.whenCompleteAsync((response, throwable) -> {
            if (throwable != null) {
                TopGGModule.getLog().error("Exception occurred while updating Top.gg stats!", throwable);
                return;
            }

            TopGGModule.getLog().success("Top.gg stats has been updated.");
        });
    }

    private String getUpdateLogMessage(int serverCount, Integer shardCount) {
        String message = "Updating Top.gg stats... (";

        message += serverCount + " servers";

        if (shardCount != null) {
            message += ", " + shardCount + " shards";
        }

        message += ")";

        return message;
    }
}
