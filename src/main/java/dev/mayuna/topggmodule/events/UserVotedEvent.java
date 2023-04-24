package dev.mayuna.topggmodule.events;

import dev.mayuna.topggsdk.api.entities.webhooks.Webhook;
import lombok.Getter;

public class UserVotedEvent implements TopGGEvent {

    private final @Getter Webhook voteInfo;

    public UserVotedEvent(Webhook voteInfo) {
        this.voteInfo = voteInfo;
    }
}
