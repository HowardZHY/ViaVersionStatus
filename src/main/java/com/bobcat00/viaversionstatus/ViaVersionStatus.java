// ViaVersionStatus - Logs players' client versions
// Copyright 2019 Bobcat00
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.bobcat00.viaversionstatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class ViaVersionStatus extends JavaPlugin
{
    ViaVersionStatus plugin = this;
    Config config;
    Listeners listeners;
    String prismVersion = "unknown";
    PrismEvent prismEvent; // Used to send event to Prism
    boolean prismHooked = false;
    int prismCounter = 0;
    
    @Override
    public void onEnable()
    {
        config = new Config(this);
        saveDefaultConfig();
        // Update old config file
        config.updateConfig();
        
        listeners = new Listeners(this);
        
        // Block "No light data found for chunk" messages
        if (config.getBlockNoLightDataWarnings())
        {
            ((Logger) LogManager.getRootLogger()).addFilter(new LogFilter("No light data found for chunk"));
        }

        // Metrics
        if (config.getEnableMetrics())
        {
            int pluginId = 4834;
            Metrics metrics = new Metrics(this, pluginId);
            
            metrics.addCustomChart(new SimplePie("connection_used",    () -> listeners.getConnectionUsed().toString()));
            metrics.addCustomChart(new SimplePie("viaversion",         () -> Bukkit.getPluginManager().isPluginEnabled("ViaVersion")                               ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("viabackwards",       () -> Bukkit.getPluginManager().isPluginEnabled("ViaBackwards")                             ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("viarewind",          () -> Bukkit.getPluginManager().isPluginEnabled("ViaRewind")                                ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("protocolsupport",    () -> Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")                          ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("notify_ops",         () -> config.getNotifyOps()               && !config.getNotifyString().isEmpty()            ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("notify_command",     () -> config.getNotifyOps()               && !config.getNotifyCommand().isEmpty()           ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("warn_players",       () -> config.getOlderVersionWarnPlayers() && !config.getOlderVersionWarnString().isEmpty()  ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("warn_command",       () -> config.getOlderVersionWarnPlayers() && !config.getOlderVersionWarnCommand().isEmpty() ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("warn_players_newer", () -> config.getNewerVersionWarnPlayers() && !config.getNewerVersionWarnString().isEmpty()  ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("warn_command_newer", () -> config.getNewerVersionWarnPlayers() && !config.getNewerVersionWarnCommand().isEmpty() ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("listener_priority",  () -> config.getHighPriority()                                                              ? "Monitor" : "Normal"));
            metrics.addCustomChart(new SimplePie("list_protocols",     () -> config.getListSupportedProtocols()                                                    ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("block_no_light",     () -> config.getBlockNoLightDataWarnings()                                                  ? "Yes" : "No"));
            metrics.addCustomChart(new SimplePie("prism_integration",  () -> config.getPrismIntegration()                                                          ? prismVersion : "No"));
            
            getLogger().info("Metrics enabled if allowed by plugins/bStats/config.yml");
        }
    }
 
    @Override
    public void onDisable()
    {
    }
    
    public void shutdown()
    {
        setEnabled(false);
    }

}
