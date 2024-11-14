package io.github.pc_com.app.core.discord;

/**
 * dicord URL parser
 */
public interface DiscordURL {
    DiscordURLType getUrlType();
    default String getGuild() { return null; }
    default String getChannel() { return null; }
    default String getMessage() { return null; }
    default String getTicket() { return null; }

    public static enum DiscordURLType {
        /** {base}/channels/{guild}/{channel} */
        CHANNEL {
            @Override
            public boolean hasGuild() {
                return true;
            }

            @Override
            public boolean hasChannel() {
                return true;
            }
        },
        /** {base}/channels/{guild}/{channel}/{message} */
        MESSAGE {
            @Override
            public boolean hasGuild() {
                return true;
            }

            @Override
            public boolean hasChannel() {
                return true;
            }

            @Override
            public boolean hasMessage() {
                return true;
            }
        },
        /** https://discord.gg/{ticket}, {base}/invite/{ticket}  */
        INVITE {
            @Override
            public boolean hasTicket() {
                return true;
            }
        };

        public boolean hasGuild() { return false; };
        public boolean hasChannel() { return false; };
        public boolean hasMessage() { return false; };
        public boolean hasTicket() { return false; };

    }
}
