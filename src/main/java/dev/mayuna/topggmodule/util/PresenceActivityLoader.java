package dev.mayuna.topggmodule.util;

import dev.mayuna.topggmodule.TopGGModule;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PresenceActivityLoader {

    private static @Getter int totalVotesCache = 0;
    private static @Getter int monthlyVotesCache = 0;
    private static @Getter long lastVotesCacheUpdate = 0;

    public static void load() {
        Config.TopGG.PresenceActivity presenceActivity = TopGGModule.getConfig().getTopgg().getPresenceActivity();

        if (!presenceActivity.isEnabled()) {
            return;
        }

        TopGGModule.getLog().info("Registering " + presenceActivity.getActivities().size() + " presence activities");

        int index = 0;
        for (String activityString : presenceActivity.getActivities()) {
            TopGGModule.getInstance().getModuleActivities().addActivity("topgg_presence_activity_index_" + index, jda -> {
                try {
                    fetchVotes().get();
                } catch (InterruptedException | ExecutionException ignored) {
                }

                return Activity.playing(activityString.replace("{total_votes}", String.valueOf(totalVotesCache)
                                                      .replace("{monthly_votes}", String.valueOf(monthlyVotesCache))));
            });

            index++;
        }
    }

    private static CompletableFuture<Void> fetchVotes() {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        long updateInterval = TopGGModule.getConfig().getTopgg().getPresenceActivity().getVoteCacheUpdateIntervalMillis();

        if (lastVotesCacheUpdate + updateInterval <= System.currentTimeMillis()) {
            TopGGModule.getLog().mdebug("Fetching vote count...");

            lastVotesCacheUpdate = System.currentTimeMillis();

            TopGGModule.getTopGGAPI().fetchBot().execute().whenCompleteAsync((bot, throwable) -> {
                completableFuture.complete(null);

                if (throwable != null) {
                    TopGGModule.getLog().error("Exception occurred while fetching bot's vote count", throwable);
                    return;
                }

                totalVotesCache = bot.getPoints();
                monthlyVotesCache = bot.getMonthlyPoints();
            });
        } else {
            completableFuture.complete(null);
        }

        return completableFuture;
    }
}
