package org.trotu.easywl;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WhitelistCommand implements SimpleCommand {
    
    private static final String PERMISSION = "easywl.admin";
    private static final String CREATOR = "Weightierharpy3";
    private static final String VERSION = "1.0";
    private final EasyWhitelist plugin;
    
    public WhitelistCommand(EasyWhitelist plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (!invocation.source().hasPermission(PERMISSION)) {
            invocation.source().sendMessage(Component.text("You don't have permission to use this command!")
                    .color(NamedTextColor.RED));
            return;
        }
        
        if (args.length == 0) {
            showHelp(invocation);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "add":
                handleAdd(invocation, args);
                break;
            case "remove":
                handleRemove(invocation, args);
                break;
            case "list":
                handleList(invocation);
                break;
            case "version":
                showVersion(invocation);
                break;
            default:
                showHelp(invocation);
                break;
        }
    }
    
    private void handleAdd(Invocation invocation, String[] args) {
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("Usage: /easywl add <username>")
                    .color(NamedTextColor.RED));
            return;
        }
        
        String playerName = args[1];
        WhitelistManager manager = plugin.getWhitelistManager();
        
        if (manager.isWhitelisted(playerName)) {
            invocation.source().sendMessage(Component.text("Player '" + playerName + "' is already whitelisted!")
                    .color(NamedTextColor.YELLOW));
            return;
        }
        
        boolean success = manager.addPlayer(playerName);
        
        if (success) {
            invocation.source().sendMessage(Component.text("Player '" + playerName + "' has been added to the whitelist!")
                    .color(NamedTextColor.GREEN));
        } else {
            invocation.source().sendMessage(Component.text("Failed to add player to the whitelist!")
                    .color(NamedTextColor.RED));
        }
    }
    
    private void handleRemove(Invocation invocation, String[] args) {
        if (args.length < 2) {
            invocation.source().sendMessage(Component.text("Usage: /easywl remove <username>")
                    .color(NamedTextColor.RED));
            return;
        }
        
        String playerName = args[1];
        WhitelistManager manager = plugin.getWhitelistManager();
        
        if (!manager.isWhitelisted(playerName)) {
            invocation.source().sendMessage(Component.text("Player '" + playerName + "' is not whitelisted!")
                    .color(NamedTextColor.YELLOW));
            return;
        }
        
        boolean success = manager.removePlayer(playerName);
        
        if (success) {
            invocation.source().sendMessage(Component.text("Player '" + playerName + "' has been removed from the whitelist!")
                    .color(NamedTextColor.GREEN));
        } else {
            invocation.source().sendMessage(Component.text("Failed to remove player from the whitelist!")
                    .color(NamedTextColor.RED));
        }
    }
    
    private void handleList(Invocation invocation) {
        WhitelistManager manager = plugin.getWhitelistManager();
        Set<String> players = manager.getWhitelistedPlayers();
        
        invocation.source().sendMessage(Component.text("--- Whitelisted Players (" + players.size() + ") ---")
                .color(NamedTextColor.GOLD));
        
        if (players.isEmpty()) {
            invocation.source().sendMessage(Component.text("No players are whitelisted! Everyone can join.")
                    .color(NamedTextColor.YELLOW));
        } else {
            StringBuilder playerList = new StringBuilder();
            int i = 0;
            
            for (String player : players) {
                playerList.append(player);
                i++;
                
                if (i < players.size()) {
                    playerList.append(", ");
                }
            }
            
            invocation.source().sendMessage(Component.text(playerList.toString())
                    .color(NamedTextColor.WHITE));
        }
    }
    
    private void showVersion(Invocation invocation) {
        invocation.source().sendMessage(Component.text("=== EasyWhitelist ===").color(NamedTextColor.GOLD));
        invocation.source().sendMessage(Component.text("Version: " + VERSION).color(NamedTextColor.WHITE));
        invocation.source().sendMessage(Component.text("Creator: " + CREATOR).color(NamedTextColor.WHITE));
        invocation.source().sendMessage(Component.text("A simple name-based whitelist for Velocity").color(NamedTextColor.YELLOW));
    }
    
    private void showHelp(Invocation invocation) {
        invocation.source().sendMessage(Component.text("--- EasyWhitelist Commands ---").color(NamedTextColor.GOLD));
        invocation.source().sendMessage(Component.text("/easywl add <username> - Add a player to the whitelist").color(NamedTextColor.WHITE));
        invocation.source().sendMessage(Component.text("/easywl remove <username> - Remove a player from the whitelist").color(NamedTextColor.WHITE));
        invocation.source().sendMessage(Component.text("/easywl list - List all whitelisted players").color(NamedTextColor.WHITE));
        invocation.source().sendMessage(Component.text("/easywl version - Show plugin version and creator information").color(NamedTextColor.WHITE));
    }
    
    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 0 || args.length == 1) {
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("list");
            suggestions.add("version");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                // Suggest whitelisted players for removal
                suggestions.addAll(plugin.getWhitelistManager().getWhitelistedPlayers());
            } else if (args[0].equalsIgnoreCase("add")) {
                // Suggest online players for adding
                plugin.getServer().getAllPlayers().forEach(player -> {
                    if (!plugin.getWhitelistManager().isWhitelisted(player.getUsername())) {
                        suggestions.add(player.getUsername());
                    }
                });
            }
        }
        
        return CompletableFuture.completedFuture(suggestions);
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(PERMISSION);
    }
}