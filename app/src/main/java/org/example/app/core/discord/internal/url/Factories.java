package org.example.app.core.discord.internal.url;

import java.net.URI;
import java.util.List;

import org.example.app.core.discord.DiscordURL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * URL parser factoryのbean定義
 */
@Configuration
public class Factories {
    @Bean
    public IURLFactory basicFactory() {
        return new IURLFactory() {
            private static final List<String> domains = List.of(
                "discord.com",
                "discordapp.com",
                "canary.discord.com"
            );

            @Override
            public boolean isSupported(URI url) {
                return domains.contains(url.getHost());
            }

            @Override
            public DiscordURL create(URI url) {
                return new BasicDiscordURL(url);
            }
        };
    }

    @Bean
    public static IURLFactory discordGgFactory() {
        return new IURLFactory() {
            private static final String HOST = "discord.gg";

            @Override
            public boolean isSupported(URI url) {
                return HOST.equals(url.getHost());
            }

            @Override
            public DiscordURL create(URI url) {
                return new DiscordGg(url);
            }
        };
    }
}
