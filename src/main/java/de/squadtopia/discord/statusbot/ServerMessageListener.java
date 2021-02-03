package de.squadtopia.discord.statusbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServerMessageListener extends ListenerAdapter {

    protected static final long DELAY_IN_MS = 60 * 1000;

    private static final Logger LOG = LoggerFactory.getLogger(ServerMessageListener.class);
    private static final String BM_BANNER_TEMPLATE = "https://cdn.battlemetrics.com/b/standardVertical/%s.png?foreground=%%23EEEEEE&linkColor=%%231185ec&lines=%%23333333&background=%%23222222&chart=players%%3ART&chartColor=%%233498db&showPlayers=&maxPlayersHeight=300&time=%d";
    private static final String DESCRIPTION_TEMPLATE = "Connect to server: steam://connect/%s\nDetails: https://www.battlemetrics.com/servers/squad/%s";
    private static final String REFRESH_UNICODE = "\uD83D\uDD04";

    private final Map<String, ServerStatus> statusMap = new HashMap<>();
    private final String battleMetricsId;
    private final String serverIp;
    private final String command;

    public ServerMessageListener(Configuration configuration) {
        Objects.requireNonNull(configuration);
        this.battleMetricsId = configuration.getBmId();
        this.serverIp = configuration.getServerIp();
        this.command = configuration.getStatusCommand();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        final String author = event.getAuthor().getName();
        final String message = event.getMessage().getContentDisplay();

        LOG.info("We received a message from {}: {}", author, message);
        if (event.getAuthor().isBot()) {
            LOG.info("Received message from bot: {}. Ignoring", author);
            return;
        }
        if (message.toLowerCase().contains("!server")) {
            final ServerStatus status = getStatus(event);
            if (status.isExpired()) {
                LOG.info("Status is expired: {}, lastUpdated: {}, currentTime: {}, delay: {}", status.isExpired(), status.getLastUpdated(), System.currentTimeMillis(), DELAY_IN_MS);
                sendServerStatus(event);
            } else {
                LOG.info("Ignoring request, as it is not expired yet");
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        LOG.info("{} reacted with {} for message {}", event.getUser().getName(), event.getReactionEmote().getName(), event.getMessageId());
        final ServerStatus status = getStatus(event);
        if (!event.getUser().isBot() && event.getMessageId().equals(status.getMessageId()) && event.getReactionEmote().getName().equals(REFRESH_UNICODE)) {
            event.getReaction().removeReaction(event.getUser()).complete();
            if (status.isExpired()) {
                updateServerStatus(event, event.getMessageId());
            }
        }
    }

    private void sendServerStatus(GenericMessageEvent event) {
        final ServerStatus status = getStatus(event);
        final MessageEmbed message = createMessage();
        event.getChannel().sendMessage(message).queue(theMessage -> {
            status.setMessageId(theMessage.getId());
            status.setLastUpdated(System.currentTimeMillis());
            event.getChannel().addReactionById(theMessage.getId(), REFRESH_UNICODE).queue();
        });
    }

    private void updateServerStatus(GenericMessageEvent event, String messageId) {
        final ServerStatus status = getStatus(event);
        event.getChannel().editMessageById(messageId, createMessage()).queue(theMessage -> {
            status.setLastUpdated(System.currentTimeMillis());
            event.getChannel().addReactionById(theMessage.getId(), REFRESH_UNICODE).queue();
        });
    }

    private ServerStatus getStatus(GenericMessageEvent event) {
        final String guildId = event.getGuild().getId();
        if (statusMap.get(guildId) == null) {
            statusMap.put(guildId, new ServerStatus());
        }
        return statusMap.get(guildId);
    }

    private MessageEmbed createMessage() {
        return new EmbedBuilder()
            .setThumbnail("https://squadtopia.de/img/SQT_Logo_small_7.png")
            .setImage(String.format(BM_BANNER_TEMPLATE, battleMetricsId, new Date().getTime()))
            .setTitle("Server Status")
            .setTimestamp(Instant.now())
            .setDescription(String.format(DESCRIPTION_TEMPLATE, serverIp, battleMetricsId))
            .build();
    }
}
