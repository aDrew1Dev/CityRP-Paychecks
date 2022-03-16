package io.github.adrew1dev.cityrppaycheck;

import io.github.adrew1dev.cityrppaycheck.listeners.VersionCheck;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public final class CityRPPaycheck extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {

        plugin = this;
        createFiles();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getServer().getPluginManager().registerEvents(new VersionCheck(), this);

        Integer delay = CityRPPaycheck.plugin.getConfig().getInt("Paycheck Frequency") * 1200;
        String topSpacer = CityRPPaycheck.plugin.getConfig().getString("TopSpacer");
        String header = CityRPPaycheck.plugin.getConfig().getString("Header");
        String subHeader = CityRPPaycheck.plugin.getConfig().getString("Subheader");
        String body = CityRPPaycheck.plugin.getConfig().getString("Body");
        String bottomSpacer = CityRPPaycheck.plugin.getConfig().getString("BottomSpacer");
        Date time = new Date();
        SimpleDateFormat tFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    for (String key : CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks").getKeys(false)) {
                        ConfigurationSection paychecksConfigSec = CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks." + key);
                        Integer amt = paychecksConfigSec.getInt("amt");
                        String perm = paychecksConfigSec.getString("perm");

                        if (player.hasPermission(perm)) {

                            if (!body.contains("%a%")) {

                                EconomyResponse r = econ.depositPlayer(player, amt);

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', topSpacer));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', header));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', subHeader));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', body));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', bottomSpacer));
                                return;
                            } else {
                                String newBody = body.replaceAll("%a%", amt.toString());

                                EconomyResponse r = econ.depositPlayer(player, amt);

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', topSpacer));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', header));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', subHeader));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', newBody));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', bottomSpacer));
                                return;
                            }

                        }
                    }
                }
            }
        }, delay, delay);

    }



    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }



    private File configf;
    private FileConfiguration config;
    public static CityRPPaycheck plugin;

    private void createFiles() {
        configf = new File(getDataFolder(), "config.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configf);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
