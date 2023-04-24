package dev.mayuna.topggmodule;

import dev.mayuna.modularbot.logging.MayuLogger;
import dev.mayuna.modularbot.objects.Module;
import dev.mayuna.topggmodule.managers.TopGGManager;
import dev.mayuna.topggmodule.util.Config;
import dev.mayuna.topggmodule.util.PresenceActivityLoader;
import dev.mayuna.topggmodule.webhook.WebhookConsumer;
import dev.mayuna.topggsdk.TopGGAPI;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

public class TopGGModule extends Module {

    private static @Getter TopGGModule instance;
    private static @Getter MayuLogger log;

    private static @Getter Config config;

    private static @Getter TopGGAPI topGGAPI;

    private static @Getter TopGGManager topGGManager;

    @Override
    public void onLoad() {
        instance = this;
        log = getLogger();

        log.info("Top.gg Module has been loaded");
        log.info("Version: " + getModuleInfo().version());
        log.info("Made by: " + getModuleInfo().author());
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        log.info("Enabling...");

        log.info("Loading configuration...");
        loadConfiguration();

        log.info("Loading top.gg API...");
        loadTopGGApi();

        loadPresenceActivities();

        log.info("Loading managers...");
        loadManagers();

        log.info("Loading done. Took " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        log.info("Disabling...");
    }

    @Override
    public void onUnload() {
        log.info("Top.gg Module has been unloaded");
        log.info("Version: " + getModuleInfo().version());
        log.info("Made by: " + getModuleInfo().author());

        instance = null;
    }

    @Override
    public void onShardManagerBuilderInitialization(@NonNull DefaultShardManagerBuilder shardManagerBuilder) {

    }

    // Other

    /**
     * Loads bot's config
     */
    private void loadConfiguration() {
        config = new Config();
        config.load();
    }

    private void loadTopGGApi() {
        Config.TopGG topGG = config.getTopgg();
        Config.TopGG.Webhook webhook = topGG.getWebhook();

        if (webhook.isEnabled()) {
            log.info("Top.gg webhook listener is enabled");
            topGGAPI = new TopGGAPI(topGG.getBotToken(), topGG.getBotId(), webhook.getPort(), webhook.getPath(), webhook.getAuthorization(), new WebhookConsumer());
        } else {
            topGGAPI = new TopGGAPI(topGG.getBotToken(), topGG.getBotToken());
        }
    }

    private void loadPresenceActivities() {
        if (config.getTopgg().getPresenceActivity().isEnabled()) {
            log.info("Top.gg presence activities are enabled");
            PresenceActivityLoader.load();
        }
    }

    private void loadManagers() {
        topGGManager = new TopGGManager();
        topGGManager.start();
    }
}
