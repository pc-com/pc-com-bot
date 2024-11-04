package org.example.app.core.discord;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.internal.utils.ChannelUtil;

@Component
@RequiredArgsConstructor
public class DiscordWebHookManager {
    public void getWebhook(Channel channel, Consumer<Webhook> then) {
        var webhookContainer = ChannelUtil.safeChannelCast(channel, IWebhookContainer.class);
        webhookContainer.retrieveWebhooks().queue(it -> get(it, webhookContainer, then));
    }

    private void get(List<Webhook> webhooks, IWebhookContainer channel, Consumer<Webhook> then) {
        var appId = channel.getJDA().getSelfUser().getIdLong();
        for (Webhook webhook : webhooks) {
            if(appId == webhook.getOwner().getIdLong()) {
                then.accept(webhook);
                return;
            }
        }
        channel.createWebhook(channel.getJDA().getSelfUser().getName()).queue(then);
    }
}
