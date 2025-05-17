package io.github.pc_com.app.core.share;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StringOriginal {

    /**
     * customIdからユーザIDを取得する
     * @param event ボタン押下イベント
     * @return ユーザID
     */
    public String parseUseridFromCustomId(ButtonInteractionEvent event){
        String customId = event.getComponentId();
        return customId.split(":")[1];
    }

    /**
     * メッセージコマンド引数パース
     * @param event メッセージ送信イベント
     * @return ユーザID
     */
    public String[] parseUseridFromMessage(String splitContent,MessageReceivedEvent event){
        String content = event.getMessage().getContentRaw();
        return content.split(splitContent);
    }
}
