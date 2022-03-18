package io.github.adrew1dev.cityrppaycheck;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String prefix = CityRPPaycheck.plugin.getConfig().getString("Prefix");
        String incorrectUsage = CityRPPaycheck.plugin.getConfig().getString("Incorrect Usage");
        String invalidPerms = CityRPPaycheck.plugin.getConfig().getString("No Permission");
        String invalidPlayer = CityRPPaycheck.plugin.getConfig().getString("Invalid Player");

        // STRING PLACEHOLDER CHECKS AND REPLACE FUNCTION
        if (prefix.contains("%p%")) {
            prefix = prefix.replaceAll("%p%", sender.getName());
        }

        if (incorrectUsage.contains("%p%")) {
            incorrectUsage = incorrectUsage.replaceAll("%p%", sender.getName());
        }

        if (invalidPerms.contains("%p%")) {
            invalidPerms = invalidPerms.replaceAll("%p%", sender.getName());
        }


        // COMMAND: /paycheck <subcmd: help/send/reload> <targetPlayer>

        if (!sender.hasPermission("cityrp.paycheck.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + invalidPerms));
            return true;
        }

        if (args == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + incorrectUsage));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l===================================="));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "                   &c&lPAYCHECKS HELP"));
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc help &8- &7View this list of commands."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc reload &8- &7Reload the plugin's configuration."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc send &ball &8- &7Manually send all players their paycheck's."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc send &b<player> &8- &7Manually send a player their paycheck."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &cCreated by aDrew1 | GitHub: aDrew1Dev"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l===================================="));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lCityRP Paychecks &8&l» &7Configuration reloaded."));
                CityRPPaycheck.plugin.reloadConfig();
                return true;

            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + incorrectUsage));
            return true;
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("help")) {

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l===================================="));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "                   &c&lPAYCHECKS HELP"));
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc help &8- &7View this list of commands."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc reload &8- &7Reload the plugin's configuration."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc send &ball &8- &7Manually send all players their paycheck's."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &e/pc send &b<player> &8- &7Manually send a player their paycheck."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l» &cCreated by aDrew1 | GitHub: aDrew1Dev"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l===================================="));
                return true;
            }

            if (args[0].equalsIgnoreCase("send")) {
                if (!args[1].equalsIgnoreCase("all")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {

                        if (invalidPlayer.contains("%p%")) {
                            invalidPlayer = invalidPlayer.replaceAll("%p%", args[1]);
                        }

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + invalidPlayer));
                        return true;
                    }

                    for (String key : CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks").getKeys(false)) {
                        ConfigurationSection paychecksConfigSec = CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks." + key);
                        int amt = paychecksConfigSec.getInt("amt");
                        String perm = paychecksConfigSec.getString("perm");

                        if (target.hasPermission(perm)) {

                            List<String> payMsg = CityRPPaycheck.plugin.getConfig().getStringList("Message");

                            List<String> plhPayMsg = new ArrayList<>();

                            EconomyResponse r = CityRPPaycheck.getEconomy().depositPlayer(target, amt);

                            for (String s : payMsg) {

                                if (!s.contains("%a%") && !s.contains("%p%")) {
                                    plhPayMsg.add(s);
                                }

                                if (s.contains("%a%") || s.contains("%p%")) {
                                    String plhString = s;
                                    plhString = s.replaceAll("%a%", Integer.toString(amt));
                                    plhString = plhString.replaceAll("%p%", Integer.toString(amt));
                                    plhPayMsg.add(plhString);
                                }
                            }

                            for (int i = 0; i < plhPayMsg.size(); i++) {
                                target.sendMessage(ChatColor.translateAlternateColorCodes('&', plhPayMsg.get(i)));
                            }

                            return true;

                        }
                    }

                    return true;
                }

                for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                    for (String key : CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks").getKeys(false)) {
                        ConfigurationSection paychecksConfigSec = CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks." + key);
                        int amt = paychecksConfigSec.getInt("amt");
                        String perm = paychecksConfigSec.getString("perm");

                        if (players.hasPermission(perm)) {

                            List<String> payMsg = CityRPPaycheck.plugin.getConfig().getStringList("Message");

                            List<String> plhPayMsg = new ArrayList<>();

                            EconomyResponse r = CityRPPaycheck.getEconomy().depositPlayer(players, amt);

                            for (String s : payMsg) {

                                if (!s.contains("%a%") && !s.contains("%p%")) {
                                    plhPayMsg.add(s);
                                }

                                if (s.contains("%a%") || s.contains("%p%")) {
                                    String plhString = s;
                                    plhString = s.replaceAll("%a%", Integer.toString(amt));
                                    plhString = plhString.replaceAll("%p%", Integer.toString(amt));
                                    plhPayMsg.add(plhString);
                                }
                            }

                            for (int i = 0; i < plhPayMsg.size(); i++) {
                                players.sendMessage(ChatColor.translateAlternateColorCodes('&', plhPayMsg.get(i)));
                            }
                            return true;

                        }
                    }
                }

            }
            return true;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + incorrectUsage));
        return true;
    }
}
