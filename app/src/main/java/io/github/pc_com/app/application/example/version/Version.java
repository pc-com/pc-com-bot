package io.github.pc_com.app.application.example.version;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Version extends ListenerAdapter {
    private final VersionDao dao;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if("/version".equals(event.getMessage().getContentRaw())) {
            event.getChannel().sendMessage(dao.getVersion()).queue();
        }
    }
}
