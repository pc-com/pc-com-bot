package org.example.app.application.urlexpansion;

import java.util.Objects;
import java.util.stream.Collectors;

import org.example.app.core.discord.DiscordURL;
import org.example.app.core.discord.DiscordURLFactory;
import org.example.app.core.discord.DiscordWebHookManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * メッセージリンク等を展開する。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class URLExpansion extends ListenerAdapter {
    private final DiscordURLFactory urlFactory;
    private final DiscordWebHookManager webhookMgr;

    @Override
    // @Hoge(bot = false, dm = false, system = false)てきな
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getAuthor().isBot()) {
            return;
        }
        var urls = urlFactory.getUrls(event.getMessage().getContentRaw());

        webhookMgr.getWebhook(event.getChannel(),
            webhook -> {
                urlFactory.gets(urls).stream()
                    .filter(Objects::nonNull)
                    .forEach(it -> sendMessage(event, webhook, it));
            }
        );
    }

    private void sendMessage(MessageReceivedEvent event, Webhook webhook, DiscordURL it) {
        event.getJDA()
            .getTextChannelById(it.getChannel())
            .retrieveMessageById(it.getMessage())
            .queue(msg -> {
                var author = msg.getAuthor();
                var msgData = MessageCreateBuilder
                    .fromMessage(msg)
                    .addEmbeds(msg.getStickers().stream().map(this::toEmbed).collect(Collectors.toList()))
                    .build();
                webhook.sendMessage(msgData)
                    .setUsername(author.getName())
                    .setAvatarUrl(author.getAvatarUrl())
                    .queue();
            });
    }

    private MessageEmbed toEmbed(Sticker sticker) {
        return new EmbedBuilder()
            .setTitle("sticker")
            .setImage(sticker.getIconUrl())
            .build();
    }
}
