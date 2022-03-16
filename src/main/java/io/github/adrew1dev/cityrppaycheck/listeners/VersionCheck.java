package io.github.adrew1dev.cityrppaycheck.listeners;

import io.github.adrew1dev.cityrppaycheck.CityRPPaycheck;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VersionCheck implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (CityRPPaycheck.plugin.getConfig().getString("Version Check") == "true") {
            Player player = e.getPlayer();

            Double plVer = CityRPPaycheck.plugin.getConfig().getDouble("Version");

            if (plVer.toString() == "1.0.0") {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c&lCityRP Paychecks &8&l» &7Plugin up to date!"));
                    return;
                }
            }
            if (player.isOp()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lCityRP Paychecks &8&l» &cALERT! &7You have an outdated version of this plugin. Visit our plugin page to update as soon as possible!"));
            }
        }

    }

}
