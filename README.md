# EasyWhitelist

A simple name-based whitelist plugin for Velocity 3.4.0.

## Features

- Case-insensitive name-based whitelist
- Whitelist stored in a simple text file
- Simple permission system with a single permission node
- Easy to use commands

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/easywl add <username>` | Add a player to the whitelist | `easywl.admin` |
| `/easywl remove <username>` | Remove a player from the whitelist | `easywl.admin` |
| `/easywl list` | List all whitelisted players | `easywl.admin` |
| `/easywl version` | Show plugin version and creator information | `easywl.admin` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `easywl.admin` | Allows access to all EasyWhitelist commands | ops |

## Installation

1. Download the latest release from the releases page
2. Place the JAR file in your Velocity server's `plugins` directory
3. Restart your server
4. Use the commands to manage your whitelist

## Building

### With Gradle
```
./gradlew clean build
```

### With Maven
```
mvn clean package
```

## Configuration

The whitelist is stored in `plugins/easywl/whitelist.txt` with one player name per line.

## License

This project is open source and available under the MIT License.

## Creator

Created by Weightierharpy3