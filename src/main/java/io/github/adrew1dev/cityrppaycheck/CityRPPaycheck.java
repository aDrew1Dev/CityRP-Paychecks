package io.github.adrew1dev.cityrppaycheck;

import io.github.adrew1dev.cityrppaycheck.listeners.VersionCheck;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class CityRPPaycheck extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {

        plugin = this;
        createFiles();

        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // INITIALIZE LISTENERS
        Bukkit.getServer().getPluginManager().registerEvents(new VersionCheck(), this);

        // INITIALIZE COMMANDS
        this.getCommand("paycheck").setExecutor(new Commands());
        this.getCommand("pc").setExecutor(new Commands());
        this.getCommand("paychecks").setExecutor(new Commands());

        Integer delay = CityRPPaycheck.plugin.getConfig().getInt("Paycheck Frequency") * 1200;

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    for (String key : CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks").getKeys(false)) {
                        ConfigurationSection paychecksConfigSec = CityRPPaycheck.plugin.getConfig().getConfigurationSection("Paychecks." + key);
                        int amt = paychecksConfigSec.getInt("amt");
                        String perm = paychecksConfigSec.getString("perm");

                        if (player.hasPermission(perm)) {

                            List<String> payMsg = CityRPPaycheck.plugin.getConfig().getStringList("Message");
                            List<String> plhPayMsg = new ArrayList<>();

                            EconomyResponse r = econ.depositPlayer(player, amt);

                            for (String s : payMsg) {
                                if (s.contains("%a%")) {
                                    String newStr = s.replaceAll("%a%", Integer.toString(amt));
                                    plhPayMsg.add(newStr);
                                } else {
                                    plhPayMsg.add(s);
                                }
                            }

                            for (int i = 0; i < plhPayMsg.size(); i++) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plhPayMsg.get(i)));
                            }
                            return;

                        }
                    }
                }
            }
        }, delay, delay);

    }


    // INITIALIZE VAULT API

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


    // INITIALIZE CONFIG

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
