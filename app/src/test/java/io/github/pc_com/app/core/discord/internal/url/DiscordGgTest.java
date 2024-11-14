package io.github.pc_com.app.core.discord.internal.url;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.Test;

public class DiscordGgTest {
    @Test
    void testGetTicket() {
        assertAll(
            () -> assertEquals(c("https://discord.gg/aaaa").getTicket(), "aaaa")
        );
    }

    DiscordGg c(String url) {
        return new DiscordGg(URI.create(url));
    }
}
