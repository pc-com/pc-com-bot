package org.example.app.core.discord;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.example.app.core.discord.internal.url.IURLFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * {@link DiscordURL}のインスタンスを生成する。
 */
@Component
@RequiredArgsConstructor
public class DiscordURLFactory {
    private final IURLFactory[] factories;

    public DiscordURL get(String urlString) {
        var url = URI.create(urlString);
        var f = Arrays.stream(factories)
            .filter(it -> it.isSupported(url))
            .findFirst();
        if(f.isPresent()) {
            return f.get().create(url);
        }
        return null;
    }

    public List<DiscordURL> gets(String... urlString) {
        return Arrays.stream(urlString)
            .map(this::get)
            .collect(Collectors.toList());
    }

    private static final Pattern URL = Pattern.compile("(https?[^\\s]+)");

    public String[] getUrls(String url) {
        var m = URL.matcher(url);
        return Stream.iterate(m, UnaryOperator.identity())
                .takeWhile(Matcher::find)
                .map(it -> it.group())
                .toArray(String[]::new);
    }
}
