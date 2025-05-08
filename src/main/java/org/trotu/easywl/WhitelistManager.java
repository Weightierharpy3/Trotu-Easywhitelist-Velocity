package org.trotu.easywl;

import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WhitelistManager {
    
    private final Path dataDirectory;
    private final Logger logger;
    private final Path whitelistFile;
    private final Set<String> whitelistedPlayers;
    
    public WhitelistManager(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.whitelistedPlayers = new HashSet<>();
        this.whitelistFile = dataDirectory.resolve("whitelist.txt");
        
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
                logger.info("Created data directory: " + dataDirectory);
            } catch (IOException e) {
                logger.error("Could not create data directory", e);
            }
        }
    }
    
    public void loadWhitelist() {
        // Clear current whitelist
        whitelistedPlayers.clear();
        
        // Create file if it doesn't exist
        if (!Files.exists(whitelistFile)) {
            try {
                Files.createFile(whitelistFile);
                logger.info("Created new whitelist file: " + whitelistFile);
            } catch (IOException e) {
                logger.error("Could not create whitelist file", e);
                return;
            }
        }
        
        // Load whitelist from file
        try (BufferedReader reader = Files.newBufferedReader(whitelistFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Add player name in lowercase for case insensitive comparison
                    whitelistedPlayers.add(line.trim().toLowerCase());
                }
            }
            logger.info("Loaded " + whitelistedPlayers.size() + " players from whitelist");
        } catch (IOException e) {
            logger.error("Could not read whitelist file", e);
        }
    }
    
    public boolean saveWhitelist() {
        try (BufferedWriter writer = Files.newBufferedWriter(whitelistFile, StandardCharsets.UTF_8)) {
            for (String player : whitelistedPlayers) {
                writer.write(player);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            logger.error("Could not save whitelist file", e);
            return false;
        }
    }
    
    public boolean addPlayer(String playerName) {
        String normalizedName = playerName.toLowerCase();
        boolean added = whitelistedPlayers.add(normalizedName);
        if (added) {
            saveWhitelist();
        }
        return added;
    }
    
    public boolean removePlayer(String playerName) {
        String normalizedName = playerName.toLowerCase();
        boolean removed = whitelistedPlayers.remove(normalizedName);
        if (removed) {
            saveWhitelist();
        }
        return removed;
    }
    
    public boolean isWhitelisted(String playerName) {
        return whitelistedPlayers.contains(playerName.toLowerCase());
    }
    
    public Set<String> getWhitelistedPlayers() {
        return new HashSet<>(whitelistedPlayers);
    }
    
    public boolean isWhitelistEmpty() {
        return whitelistedPlayers.isEmpty();
    }
}