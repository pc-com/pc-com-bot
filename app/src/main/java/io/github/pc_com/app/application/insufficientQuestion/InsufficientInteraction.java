package io.github.pc_com.app.application.insufficientQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsufficientInteraction extends ListenerAdapter {
    private static final Map<String, String> allData = new HashMap<>();
    private static final Map<String, String> softwareData = new HashMap<>();
    private static final Map<String, String> hardwareData = new HashMap<>();
    @Value("${role.bestanswer}")
    private Long bestAnswerRole;

    static {
        allData.put("title", "- タイトルは__**一目で内容が分かる**__ようにお書きください。");
        allData.put("multipost", "- マルチポストは禁止です。");
        allData.put("multiple", "- 1質問1スレ(同じ質問を複数スレ建てない)を遵守してください。");
        allData.put("purpose", """
                - __**抽象的な質問は控えてください。**__
                  - これらを記載してください。
                  - 目的
                  - 目的の解決手段として現在取り組んでいること。
                  - 現状
                - 悪い例: ××ってどうやったらできるか
                  - 目的がわからないので、他の解決手段の提案ができません。
                - 良い例: ○○をしたい。そのために××をしたいのでどのようにやる方法があるか。現状は〜〜
                """);
        allData.put("os_model", "- 質問時はOSや型番等わかる情報を細かくお書きください。");
        allData.put("know", "- 質問前に調べた事や調べてわかったことを記載してください。");
        allData.put("practice", "- 質問前に実践した事を記載してください。");
        allData.put("search", "- 必ず__**質問前にググって**__ください。\nhttps://google.co.jp/");

        softwareData.put("code", "- __**コードはコピペで全文を貼り付け**__ること(スクショだと回答者の環境で再現するのが手間です。)");
        softwareData.put("error", "- __**エラーはコピペで全文を貼り付け**__ること");
        softwareData.put("codeblock",
                "- コードはシンタックスハイライトをつけること\nシンタックスハイライト付け方\n```lang(py,js,rb等)\nコード\n```");
        softwareData.put("os", "- 必ずこれらの__**名前とバージョンを記載**__すること\n - OS(windows 11, Ubuntu 24.04, など)");
        softwareData.put("library",
                "- 必ずこれらの__**名前とバージョンを記載**__すること\n- language(Python 3.13, Node.js 23, など)\n- サードパーティライブラリ(pipやnpmなどでインストールしたライブラリ群)");

        hardwareData.put("pc_details", "- PCの詳細を記載すること(構成など)");
        hardwareData.put("os", "- OS(Windows 11, Ubuntu 24.04, など)");
        hardwareData.put("model", "- 購入したPCならば型番など");
        hardwareData.put("symptoms", "- 詳しい症状と試したこと(問題の起きているパーツが判明しているならばそのパーツを書いておくことが望ましい)");
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().startsWith("question_insufficient_")) {
            return;
        }
        category(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("guideline_ok_")) {
            return;
        }
        String[] parts = event.getComponentId().split("_", 3);
        var ownerId = parts[2];

        if (event.getUser().getId().equals(ownerId)) {
            event.reply(
                    "投稿者は実行できません。")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (!event.getMember().getRoles().stream().anyMatch(role -> bestAnswerRole.equals(role.getIdLong()))) {
            event.reply("ベストアンサー以外は実行できません。")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        event.editMessage(new MessageEditBuilder()
                .setComponents()
                .setEmbeds(
                        new EmbedBuilder()
                                .setTitle("❓ 質問が投稿されました")
                                .setDescription("> ```ガイドラインの条件を満たしています```")
                                .setColor(0x1f982d)
                                .setFooter(String.format("Confirmed By @%s", event.getUser().getName()))
                                .build())
                .build()).queue();
    }

    private void category(StringSelectInteractionEvent event) {
        String[] parts = event.getComponentId().split("_", 4);
        if (parts.length < 3) {
            event.reply("不明な処理です").setEphemeral(true).queue();
            return;
        }
        var category = parts[2];
        var ownerId = parts[3];

        if (event.getUser().getId().equals(ownerId)) {
            event.reply(
                    "投稿者は実行できません。")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        var message = getContent(category, event);

        event.editMessageEmbeds(event.getMessage().getEmbeds().get(getIndex(category)),
                getEmbed(message, category, event)).queue();
        event.getChannel().sendMessage(
                String.format("<@!%s> より下記事項が足りないと指摘されました%n%s", ownerId, message))
                .queue();

    }

    private Map<String, String> getCategoryData(String category) {
        return switch (category) {
            case "all" -> allData;
            case "software" -> softwareData;
            case "hardware" -> hardwareData;
            default -> Map.of();
        };
    }

    private int getIndex(String category) {
        return switch (category) {
            case "software" -> 1;
            case "hardware" -> 2;
            default -> 0;
        };
    }

    private MessageEmbed getEmbed(String message, String category, StringSelectInteractionEvent event) {
        var embed = new EmbedBuilder(event.getMessage().getEmbeds().get(1));
        var title = switch (category) {
            case "software" -> "📄プログラムに関する質問について";
            case "hardware" -> "💻 PCに関する質問について";
            default -> "❓ 全質問共通";
        };
        embed.setColor(0xfd5757);
        embed.getFields().set(getIndex(category), new Field(title, message, false));
        return embed.build();

    }

    private String getContent(String category, StringSelectInteractionEvent event) {
        Map<String, String> data = getCategoryData(category);

        List<String> messageList = new ArrayList<>();

        List<String> values = event.getValues();
        for (String value : values) {
            if (data.containsKey(value)) {
                messageList.add(data.get(value));
            }
        }

        String description = event.getMessage().getEmbeds().get(1).getFields().get(getIndex(category)).getValue();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            description = description
                    .replace(entry.getKey(), entry.getValue())
                    .replace("> ```回答者により、この投稿の不足部分が表示されます```", "");
        }

        messageList.add(description);

        StringBuilder finalMessage = new StringBuilder();
        for (String message : messageList) {
            finalMessage.append(message).append("\n");
        }

        String[] lines = finalMessage.toString().split("\n");
        Set<String> seenMessages = new HashSet<>();
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (!seenMessages.contains(line)) {
                seenMessages.add(line);
                result.append(line).append("\n");
            }
        }

        return result.toString().trim();
    }

}
