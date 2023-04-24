package dev.mayuna.topggmodule.util;

import com.google.gson.Gson;
import dev.mayuna.mayusjsonutils.objects.MayuJson;
import dev.mayuna.modularbot.objects.ModuleConfig;
import dev.mayuna.topggmodule.TopGGModule;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class Config {

    private @Getter TopGG topgg = new TopGG();

    public void load() {
        ModuleConfig moduleConfig = TopGGModule.getInstance().getModuleConfig();
        moduleConfig.copyDefaultsIfEmpty();
        moduleConfig.reload();

        MayuJson mayuJson = moduleConfig.getMayuJson();
        Gson gson = new Gson();

        topgg = gson.fromJson(mayuJson.getOrCreate("topgg", gson.toJsonTree(topgg)), TopGG.class);

        moduleConfig.save();
        TopGGModule.getLog().info("Config loaded");
    }

    public static class TopGG {

        private @Getter String botToken = "BOT_TOKEN_HERE";
        private @Getter String botId = "BOT_ID_HERE";

        private @Getter Webhook webhook = new Webhook();
        private @Getter PresenceActivity presenceActivity = new PresenceActivity();
        private @Getter Updates updates = new Updates();

        public static class Updates {

            private @Getter long startupDelayMillis = 60000;
            private @Getter long updateIntervalMillis = 3600000;
            private @Getter boolean updateShardCount = true;
        }

        public static class Webhook {

            private @Getter boolean enabled = false;
            private @Getter int port = 8080;
            private @Getter String path = "/topgg-webhook";
            private @Getter String authorization = "foobazz";
        }

        public static class PresenceActivity {

            private @Getter boolean enabled = false;
            private @Getter long voteCacheUpdateIntervalMillis = 1800000;
            private @Getter List<String> activities = new LinkedList<>();
        }
    }
}
