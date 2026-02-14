# cli-stash

A CLI tool for saving, searching, and running frequently used shell commands. Works as a personal command manager right from your terminal.

## Requirements

- Java 17+
- Maven 3.x
- Docker (for Docker-based installation)

## Installation

### Via Docker (recommended)

```bash
./install.sh
```

The script will:
1. Build the JAR via Maven
2. Build the Docker image
3. Create a Docker volume `cli_stash_data` for persistent storage
4. Install a wrapper script to `/usr/local/bin/cli-stash`

After that `cli-stash` is available globally from any directory.

### Locally (without Docker)

```bash
mvn clean package -DskipTests
java -jar target/cli-stash.jar
```

## Usage

### Two modes of operation

**Single-command mode** — pass a command as an argument:

```bash
cli-stash -add build "mvn clean install"
```

**Interactive mode (REPL)** — run without arguments:

```bash
cli-stash
stash> -add build mvn clean install
stash> -list
stash> exit
```

## Commands

| Command | Description |
|---------|-------------|
| `-list` | Show all saved commands |
| `-get <name>` | Retrieve a command by name (increments usage counter) |
| `-add <name> <cmd>` | Save a new command |
| `-update <name> <cmd>` | Update an existing command |
| `-remove <name>` | Delete a command |
| `-run <name>` | Find and execute a command (increments usage counter) |
| `-help` | Show help |

### Examples

```bash
# Save a command
cli-stash -add deploy "docker compose up -d"

# List all commands (sorted by usage count)
cli-stash -list

# Output:
# NAME                 USES     CREATED              COMMAND
# deploy               3        14.02.2026 10:30     docker compose up -d
# build                1        14.02.2026 09:15     mvn clean install

# Retrieve a command (for copying)
cli-stash -get deploy
# docker compose up -d

# Execute a command directly
cli-stash -run deploy

# Update a command
cli-stash -update deploy "docker compose up -d --build"

# Delete a command
cli-stash -remove deploy
```

## Data Storage

Commands are persisted to a `commands.json` file:
- **Docker**: `/data/commands.json` (persistent volume)
- **Local**: `commands.json` in the current working directory

## Docker Compose

```bash
# Start interactive mode
docker compose run --rm cli-stash

# Run a single command
docker compose run --rm cli-stash -list
```

## Project Structure

```
cli-stash/
├── src/main/java/com/clistash/app/
│   ├── App.java            # Entry point, CLI parser, REPL
│   ├── CommandRunner.java  # Shell command execution
│   ├── CommandStore.java   # CRUD operations, JSON read/write
│   └── model/
│       ├── Command.java    # Command interface
│       └── CommandImpl.java# Implementation with metadata
├── Dockerfile
├── docker-compose.yml
├── install.sh              # Installation script
└── pom.xml
```

## Tech Stack

- Java 17
- Jackson (JSON serialization)
- Maven Shade Plugin (fat JAR)
- Docker