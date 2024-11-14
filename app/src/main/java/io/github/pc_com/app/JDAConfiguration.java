package io.github.pc_com.app;

import java.util.EnumSet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kurages
 */
@Configuration
public class JDAConfiguration {
    @Bean(destroyMethod = "shutdownNow")
    public static JDA jda(
            DiscordConfig config,
            EventListener[] listeners) {
        return JDABuilder
            .createDefault(config.token())
            .enableIntents(EnumSet.allOf(GatewayIntent.class))
            .addEventListeners((Object[]) listeners)
            .build();
    }
}
