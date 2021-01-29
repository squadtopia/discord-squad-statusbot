package de.squadtopia.discord.statusbot;

import java.util.Objects;

public class Configuration {
    private final String token;
    private final String bmId;
    private final String serverIp;
    private final String statusCommand;

    private Configuration(String args[]) {
        this(args[0], args[1], args[2], args[3]);
    }

    private Configuration(String token, String bmId, String serverIp, String statusCommand) {
        this.token = Objects.requireNonNull(token);
        this.bmId = Objects.requireNonNull(bmId);
        this.serverIp = Objects.requireNonNull(serverIp);
        this.statusCommand = Objects.requireNonNull(statusCommand);
    }

    public String getToken() {
        return token;
    }

    public String getBmId() {
        return bmId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getStatusCommand() {
        return statusCommand;
    }

    public static Configuration parse(String[] args) {
        if (args == null || args.length < 4) {
            throw new RuntimeException("usage: bot.jar <token> <battlemetrics id> <server ip> <status command>");
        }
        for (String eachArg : args) {
            if (eachArg == null || "".equals(eachArg.trim())) {
                throw new RuntimeException("Provided argument is empty or null, but should not be empty or null");
            }
        }
        if (!args[3].startsWith("!") && !args[3].startsWith("?")) {
            throw new RuntimeException("statusCommand must be provided, e.g. !server");
        }

        return new Configuration(args);
    }
}
