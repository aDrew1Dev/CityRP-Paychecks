package io.github.adrew1dev.cityrppaycheck.listeners;

import io.github.adrew1dev.cityrppaycheck.CityRPPaycheck;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VersionCheck implements Listener {

    private boolean verCheck() {
        return CityRPPaycheck.plugin.getConfig().getBoolean("Version Check");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {

        if (verCheck()) {

            if (joinEvent.getPlayer().isOp()) {

                String currentVer = CityRPPaycheck.plugin.getConfig().getString("Version");
                String prefix = "&c&lCityRP: Paychecks &8&l» ";

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=100745").openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");
                    String ver = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                    if (ver == currentVer) {
                        joinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                prefix + "&e'CityRP: Paychecks' &ais up to date! (" + currentVer + ")"));
                    } else {
                        joinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                prefix + "&cNOTICE! &e'CityRP: Paychecks' &cis out of date! Please update to " + ver +  " from SpigotMC!"));
                    }

                } catch (IOException e) {
                    joinEvent.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            prefix + "&cError! &e'CityRP: Paychecks' &cfailed to connect to SpigotMC.org. Could not perform version check."));
                    e.printStackTrace();

                }
            }
        }

    }

}
