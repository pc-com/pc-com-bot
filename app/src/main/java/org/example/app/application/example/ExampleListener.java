package org.example.app.application.example;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @author kurages
 */
@Slf4j
@Component
public class ExampleListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        log.info("[{}] {}", event.getMessage().getAuthor().getName(),
            event.getMessage().getContentRaw());
    }
}
