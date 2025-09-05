package com.mogdeveloping.mogac.commands;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MOGACCommand implements CommandExecutor {

    private final MOGAC plugin;

    public MOGACCommand(MOGAC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mogac.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("help")) {
            sendHelp(sender);
        } else if (subCommand.equals("checks")) {
            listChecks(sender);
        } else if (subCommand.equals("toggle")) {
            toggleCheck(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if (subCommand.equals("reload")) {
            sender.sendMessage(ChatColor.GREEN + "MOG-ANTICHEAT has been reloaded.");
        } else if (subCommand.equals("alerts")) {
            toggleAlerts(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand. Type /mogac help for help.");
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "MOG-ANTICHEAT " + ChatColor.GRAY + "by MOG-Developing");
        sender.sendMessage(ChatColor.GRAY + "Commands:");
        sender.sendMessage(ChatColor.RED + "/mogac help " + ChatColor.GRAY + "- Show this help message");
        sender.sendMessage(ChatColor.RED + "/mogac checks " + ChatColor.GRAY + "- List all checks");
        sender.sendMessage(ChatColor.RED + "/mogac toggle <check> " + ChatColor.GRAY + "- Toggle a check");
        sender.sendMessage(ChatColor.RED + "/mogac alerts " + ChatColor.GRAY + "- Toggle violation alerts");
        sender.sendMessage(ChatColor.RED + "/mogac reload " + ChatColor.GRAY + "- Reload the plugin");
    }

    private void listChecks(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "MOG-ANTICHEAT Checks:");
        
        for (Check check : plugin.getCheckManager().getChecks()) {
            String status = check.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
            sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.RED + check.getName() + 
                               ChatColor.GRAY + " [" + status + ChatColor.GRAY + "]");
        }
    }

    private void toggleCheck(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /mogac toggle <check>");
            return;
        }

        String checkName = args[0];
        List<Check> checks = plugin.getCheckManager().getChecks();
        
        for (Check check : checks) {
            if (check.getName().equalsIgnoreCase(checkName)) {
                check.setEnabled(!check.isEnabled());
                
                String status = check.isEnabled() ? "enabled" : "disabled";
                sender.sendMessage(ChatColor.GREEN + "Check " + check.getName() + " has been " + status + ".");
                return;
            }
        }
        
        sender.sendMessage(ChatColor.RED + "Check " + checkName + " not found.");
    }

    private void toggleAlerts(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return;
        }

        Player player = (Player) sender;
        
        if (player.hasPermission("mogac.alerts")) {
            boolean hasPermission = player.hasPermission("mogac.alerts.toggle");
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), 
                "pex user " + player.getName() + " " + (hasPermission ? "remove" : "add") + " mogac.alerts.toggle");
            
            String status = hasPermission ? "disabled" : "enabled";
            player.sendMessage(ChatColor.GREEN + "Violation alerts have been " + status + ".");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have permission to receive violation alerts.");
        }
    }
}