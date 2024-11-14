package io.github.pc_com.app.core.discord.internal.url;

import java.net.URI;

import io.github.pc_com.app.core.discord.DiscordURL;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 招待ショートリンク用の解析処理。
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DiscordGg implements DiscordURL {
    private final URI url;

    @Override
    public DiscordURLType getUrlType() {
        return DiscordURLType.INVITE;
    }

    @Override
    public String getTicket() {
        return url.getPath().substring(1);
    }
}
