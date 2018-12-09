package io.github.winx64.sse.command;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class CommandReload implements CommandExecutor {

    private final SignConfiguration signConfig;
    private final SignMessage signMessage;

    public CommandReload(SmartSignEditor plugin) {
        this.signConfig = plugin.getSignConfig();
        this.signMessage = plugin.getSignMessage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (signConfig.initializeConfiguration() && signMessage.initializeConfiguration()) {
            sender.sendMessage(signMessage.get(NameKey.COMMAND_RELOAD_SUCCESS));
        } else {
            sender.sendMessage(signMessage.get(NameKey.COMMAND_RELOAD_FAILURE));
        }
        return true;
    }
}
