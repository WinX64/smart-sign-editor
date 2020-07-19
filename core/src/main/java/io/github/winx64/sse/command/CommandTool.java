package io.github.winx64.sse.command;

import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandTool implements CommandExecutor {

    private final SignConfiguration signConfig;
    private final SignMessage signMessage;

    public CommandTool(SignConfiguration signConfig, SignMessage signMessage) {
        this.signConfig = signConfig;
        this.signMessage = signMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(signMessage.get(SignMessage.Message.COMMAND_NO_CONSOLE));
            return true;
        }

        Player player = (Player) sender;
        player.getInventory().addItem(signConfig.getToolItem());
        return true;
    }

}
