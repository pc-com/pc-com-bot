package io.github.pc_com.app.core.discord.internal.url;

import java.net.URI;

import io.github.pc_com.app.core.discord.DiscordURL;

public interface IURLFactory {
    boolean isSupported(URI url);
    DiscordURL create(URI url);
}
