package io.github.pc_com.app;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kurages
 */
@ConfigurationProperties(prefix = "discord")
public record DiscordConfig (
    String token
) {
    public DiscordConfig {
        Objects.requireNonNull(token);
    }
}
