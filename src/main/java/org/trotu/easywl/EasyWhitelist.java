package org.trotu.easywl;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.command.CommandMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "easywl",
        name = "EasyWhitelist",
        version = "1.0-SNAPSHOT",
        description = "A simple name-based whitelist plugin for Velocity",
        authors = {"Trotu"}
)
public class EasyWhitelist {
    
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private WhitelistManager whitelistManager;
    
    @Inject
    public EasyWhitelist(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        whitelistManager = new WhitelistManager(dataDirectory, logger);
        whitelistManager.loadWhitelist();
        
        WhitelistCommand whitelistCommand = new WhitelistCommand(this);
        CommandMeta meta = server.getCommandManager().metaBuilder("easywl")
                .plugin(this)
                .build();
        server.getCommandManager().register(meta, whitelistCommand);
        
        logger.info("EasyWhitelist has been initialized!");
    }
    
    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        
        // Check if whitelist is empty (allow everyone)
        if (whitelistManager.isWhitelistEmpty()) {
            return;
        }
        
        if (!whitelistManager.isWhitelisted(playerName)) {
            event.setResult(LoginEvent.ComponentResult.denied(
                    Component.text("You are not whitelisted on this server!")
                            .color(NamedTextColor.RED)
            ));
        }
    }
    
    public ProxyServer getServer() {
        return server;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Path getDataDirectory() {
        return dataDirectory;
    }
    
    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
}