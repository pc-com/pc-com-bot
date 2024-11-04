package org.example.app.system;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.example.app.SlashCommand;
import org.springframework.stereotype.Component;

/**
 * @author kurages
 */
@Component
@RequiredArgsConstructor
public class SlashCommandRegister extends ListenerAdapter {
    private final List<SlashCommand> commands;

    @Override
    public void onReady(ReadyEvent event) {
        this.commands.stream()
            .map(SlashCommand::slashCommandData)
            .map(event.getJDA()::upsertCommand)
            .forEach(RestAction::queue);
    }
}
