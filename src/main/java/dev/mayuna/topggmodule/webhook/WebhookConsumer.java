package dev.mayuna.topggmodule.webhook;

import dev.mayuna.topggmodule.TopGGModule;
import dev.mayuna.topggmodule.events.UserVotedEvent;
import dev.mayuna.topggsdk.api.entities.webhooks.Webhook;

import java.util.function.Consumer;

public class WebhookConsumer implements Consumer<Webhook> {

    @Override
    public void accept(Webhook webhook) {
        TopGGModule.getLog().mdebug("Received Top.gg vote update from user " + webhook.getUserId());
        TopGGModule.getEventBus().post(new UserVotedEvent(webhook));
    }
}
