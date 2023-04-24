# Top.gg Module
This module can update [top.gg](https://top.gg/) bot stats and listen for new user votes.

## Installation
1. Drop module's jar into `/modules/` folder
2. Create folder `Top.gg Module` in `/modules/` folder
3. Copy default config from [code source](resources/config.json) to `/modules/Top.gg Module` folder
4. Change config to your liking (set bot token from bot's top.gg management panel, bot id, etc.)
5. Start Module Discord Bot

All update logs are in log level `MDEBUG`, which can be viewed in Modular Discord Bot's logs.

## Usage
### Listening to user's votes
1. Enable webhook listener in config
2. Configure it by your liking
3. Set the webhook url in bot's top.gg management panel
4. Register UserVotedEvent listener (code)

```java
// Should be called once, for example in #onEnable() method

public void registerTopGGModuleListener() {
    EventBus eventBus = TopGGModule.getInstance().getEventBus();
    eventBus.register(this); // Or class where method #onUserVote() with annotation @Subscribe is
}

@Subscribe
public void onUserVote(UserVotedEvent event) {
    Webhook voteInfo = event.getVoteInfo();
    
    voteInfo.getUserId(); // Etc.
}
```
