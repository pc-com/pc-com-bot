package io.github.pc_com.app.core.discord.internal.url;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.pc_com.app.core.discord.DiscordURL;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 一般的なdiscord URL解析処理
 */
@Slf4j @Getter
class BasicDiscordURL implements DiscordURL {
    private static final Pattern CHANNEL_PATH = Pattern.compile("^/channels/(\\d+)(?:/(\\d+))?(?:/(\\d+))?/?$");
    private final URI url;
    private final DiscordURLType urlType;

    BasicDiscordURL(URI uri) {
        this.url = uri;
        this.urlType = getType(uri);
    }

    private DiscordURLType getType(URI uri) {
        var path = uri.getPath();
        if(path.startsWith("/channels/")) {
            Matcher m = CHANNEL_PATH.matcher(path);
            if(m.matches()) {
                return switch (m.groupCount()) {
                    case 2 -> DiscordURLType.CHANNEL;
                    case 3 -> DiscordURLType.MESSAGE;
                    default -> null;
                };
            }
        } else if(path.startsWith("/invite/")) {
            return DiscordURLType.INVITE;
        }
        return null;
    }

    @Override
    public String getGuild() {
        if(urlType.hasGuild()) {
            var m = CHANNEL_PATH.matcher(url.getPath());
            if (m.matches()) {
                return m.group(1);
            }
        }
        return null;
    }

    @Override
    public String getChannel() {
        if(urlType.hasChannel()) {
            var m = CHANNEL_PATH.matcher(url.getPath());
            if (m.matches()) {
                return m.group(2);
            }
        }
        return null;
    }

    @Override
    public String getMessage() {
        if(urlType.hasMessage()) {
            var m = CHANNEL_PATH.matcher(url.getPath());
            if (m.matches()) {
                return m.group(3);
            }
        }
        return null;
    }

    @Override
    public String getTicket() {
        if(urlType.hasTicket()) {
            return url.getPath().substring("/invite/".length());
        }
        return null;
    }
}
