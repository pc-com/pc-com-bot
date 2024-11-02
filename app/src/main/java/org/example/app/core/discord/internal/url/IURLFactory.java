package org.example.app.core.discord.internal.url;

import java.net.URI;

import org.example.app.core.discord.DiscordURL;

public interface IURLFactory {
    boolean isSupported(URI url);
    DiscordURL create(URI url);
}
