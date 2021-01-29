package de.squadtopia.discord.statusbot;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        final Configuration configuration = Configuration.parse(args);
        final JDABuilder builder = JDABuilder.createDefault(configuration.getToken());
        builder.addEventListeners(new ServerMessageListener(configuration));
        builder.build();
    }

}



