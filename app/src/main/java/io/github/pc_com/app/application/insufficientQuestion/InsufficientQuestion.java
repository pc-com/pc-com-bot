package io.github.pc_com.app.application.insufficientQuestion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsufficientQuestion extends ListenerAdapter {
    private final Long questionChannelId = 1019989168283197483L;
    private final String guideLineUrl = "https://discord.com/channels/932529116400459786/1217675613679124530";
    private final String guideLineImageUrl = "https://cdn.discordapp.com/attachments/1160149457069944892/1160156625739468850/BestAnswer.png";

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (!event.isFromType(ChannelType.GUILD_PUBLIC_THREAD)) {
            return;
        }
        ThreadChannel channel = event.getChannel().asThreadChannel();
        if (!questionChannelId.equals(channel.getParentChannel().getIdLong())) {
            return;
        }
        sendMessage(channel);
    }

    private void sendMessage(ThreadChannel thread) {
        var messageData = new MessageCreateBuilder()
                .addContent(String.format("<@%s> **質問する際のガイドラインは確認しましたか？**\n%s", thread.getOwnerId(), guideLineUrl))
                .addEmbeds(getEmbeds()).addComponents(getComponents(thread.getOwnerId()))
                .build();
        thread.sendMessage(messageData).queue();
    }

    private ActionRow[] getComponents(String threadOwnerId) {
        ActionRow[] components = {
                ActionRow.of(
                        StringSelectMenu
                                .create(String.format("question_insufficient_all_%s", threadOwnerId))
                                .setPlaceholder("全質問共通から不足部を選択")
                                .setMinValues(1)
                                .setMaxValues(8)
                                .addOptions(
                                        SelectOption.of("タイトル", "title"),
                                        SelectOption.of("マルチポスト", "multipost"),
                                        SelectOption.of("複数質問", "multiple"),
                                        SelectOption.of("目的 or 現状", "purpose"),
                                        SelectOption.of("OS or 型番", "os_model"),
                                        SelectOption.of("分かっていること不足", "know"),
                                        SelectOption.of("実践したこと不足", "practice"),
                                        SelectOption.of("検索不足", "search"))
                                .build()),
                ActionRow.of(
                        StringSelectMenu
                                .create(String.format("question_insufficient_software_%s", threadOwnerId))
                                .setPlaceholder("ソフトウェア系から不足部を選択")
                                .setMinValues(1)
                                .setMaxValues(5)
                                .addOptions(
                                        SelectOption.of("コード不足", "code"),
                                        SelectOption.of("エラー不足", "error"),
                                        SelectOption.of("コードブロック不足", "codeblock"),
                                        SelectOption.of("OS名 or バージョン", "os"),
                                        SelectOption.of("ライブラリ名 or バージョン", "library"))
                                .build()),
                ActionRow.of(
                        StringSelectMenu
                                .create(String.format("question_insufficient_hardware_%s", threadOwnerId))
                                .setPlaceholder("ハードウェア系から不足部を選択")
                                .setMinValues(1)
                                .setMaxValues(4)
                                .addOptions(
                                        SelectOption.of("PC詳細", "pc_details"),
                                        SelectOption.of("OS不足", "os"),
                                        SelectOption.of("型番不足", "model"),
                                        SelectOption.of("詳しい症状", "symptoms"))
                                .build()),
                ActionRow.of(
                        Button
                                .secondary(String.format("guideline_ok_%s", threadOwnerId), "GuideLine OK!")
                                .withEmoji(Emoji.fromUnicode("✅")))

        };
        return components;
    }

    private List<MessageEmbed> getEmbeds() {
        String[] texts = {
                "### ❓ 質問が投稿されました",
                "質問が解決しましたら、__**解決に一番役立ったメッセージ**__ を `右クリック` または `長押し`をして、`アプリ` の項目から `BestAnswer` を選択してください。",
                "※ `BestAnswer` を実行すると自動で質問がクローズします。"
        };
        EmbedBuilder[] embeds = {
                new EmbedBuilder()
                        .setDescription(String.join("\n", texts))
                        .setColor(0x0099FF)
                        .setImage(guideLineImageUrl),
                new EmbedBuilder()
                        .setColor(0xf1c40f)
                        .addField("❓ 全質問共通", "> ```回答者により、この投稿の不足部分が表示されます```", false)
                        .addField("📄 プログラムに関する質問について", "> ```回答者により、この投稿の不足部分が表示されます```", false)
                        .addField("💻 PCに関する質問について", "> ```回答者により、この投稿の不足部分が表示されます```", false)
        };

        return Arrays.stream(embeds).map(embed -> embed.build()).collect(Collectors.toList());
    }

}
