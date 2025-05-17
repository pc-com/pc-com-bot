package io.github.pc_com.app.application.manageTicket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.pc_com.app.core.share.StringOriginal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManageTicket extends ListenerAdapter {
    private static final String CreateTicketCustomId = "createTicket";
    private static final String CloseTicketCustomId = "closeTicket";
    private static final String ExecuteTicketCustomId = "executeTicket";
    private static final String CancelTicketCustomId = "cancelTicket";
    private static final StringOriginal str = new StringOriginal();

    @Value("${role.moderator}")
    private final Long ModRole;


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        //チケット作成
        if (event.getComponentId().equals(CreateTicketCustomId)) {
            createTicket(event);
        } else if (event.getComponentId().startsWith(CloseTicketCustomId)){
            sendConfirmMessage(event);
        } else if (event.getComponentId().startsWith(ExecuteTicketCustomId)){
            executeClose(event);
        } else if (event.getComponentId().startsWith(CancelTicketCustomId)){
            cancelClose(event);
        }
    }

    @Override
    // @Hoge(bot = false, dm = false, system = false)てきな
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = str.parseUseridFromMessage(" ", event);
        String channelId = args[0];

        TextChannel channel = event.getJDA().getTextChannelById(channelId);
        if (channel == null)return;

        Button createTicketButton = Button.success(CreateTicketCustomId, "チケット発行").withEmoji(Emoji.fromUnicode("✉️"));
        event.getChannel().sendMessageComponents(ActionRow.of(createTicketButton)).queue();
    }

    // ###########################チケット作成###########################


    /**
     * チケットを作成し、クローズボタンを送信する
     * @param event　ボタン押下イベント
     */
    private void createTicket(ButtonInteractionEvent event){
        event.deferEdit().queue();

        Member member = event.getMember();
        if (member == null) return;

        TextChannel channel = event.getChannel().asTextChannel();
        ThreadChannelAction threadAction =  channel.createThreadChannel(member.getEffectiveName().concat("-ticket"), true).setInvitable(false);

        threadAction.queue(thread -> afterCreateThread(thread, event));
    }


    /**
     * モデレータロールとチケット発行者を追加する
     * @param event　ボタン押下イベント
     */
    private void afterCreateThread(ThreadChannel thread,ButtonInteractionEvent event) {
        thread.addThreadMember(event.getUser()).queue();
        thread.sendMessage("<@&".concat(ModRole.toString().concat(">"))).queue();

        User user = event.getUser();

        EmbedBuilder embed = new EmbedBuilder().appendDescription("クローズボタンを押してチケットをクローズできます");
        Button closeButton = Button.success(CloseTicketCustomId.concat(":").concat(user.getId()), "CLOSE").withEmoji(Emoji.fromUnicode("⛔"));

        thread.sendMessageEmbeds(embed.build()).setContent(user.getAsMention()).setComponents(ActionRow.of(closeButton)).queue();
    }

    // ###########################クローズ###########################


    /**
     * クローズ確認メッセージを送信する
     * @param event　ボタン押下イベント
     */
    private void sendConfirmMessage(ButtonInteractionEvent event){
        String createUserId = str.parseUseridFromCustomId(event);

        EmbedBuilder embed = new EmbedBuilder().appendDescription("クローズボタンを押してチケットをクローズできます");
        Button executeButton = Button.success(ExecuteTicketCustomId.concat(":").concat(createUserId), "クローズ");
        Button cancelButton = Button.danger(CancelTicketCustomId.concat(":").concat(createUserId), "キャンセル");

        event.replyEmbeds(embed.build()).setComponents(ActionRow.of(executeButton, cancelButton)).queue();
    }


    /**
     * クローズ
     * @param event　ボタン押下イベント
     */
    private void executeClose(ButtonInteractionEvent event){
        event.deferEdit().queue();
        String createUserId = str.parseUseridFromCustomId(event);

        User createuser = event.getJDA().getUserById(createUserId);
        if (createuser == null) return;

        ThreadChannel thread = event.getChannel().asThreadChannel();
        thread.removeThreadMember(createuser);
        thread.getManager().setArchived(true).queue();
    }


    /**
     * クローズキャンセル
     * @param event　ボタン押下イベント
     */
    private void cancelClose(ButtonInteractionEvent event){
        event.deferEdit().queue();
        event.getMessage().delete().queue();
    }


}
