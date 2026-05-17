# KotlinFramework
A small framework for writing Paper plugins in Kotlin without the usual boilerplate.

I built this because I got tired of copy-pasting the same companion object, instance, and registerEvents setup into every plugin. It's not trying to replace anything, it just cuts out the repetitive parts.

---

## What it does

- Base class (`KotlinFramework`) that handles `instance`, logging, and safe startup/shutdown
- Inline command registration: no separate class needed for simple commands
- Inline event listening: no `@EventHandler`, no separate Listener class
- `KotlinCommand` base class for commands that need their own file
- Scheduler helpers: `runLater`, `runTimer`, `runAsync`
- Config helpers: `saveLocation`, `getLocation`, `config`, `setConfig`
- Message helpers: `msg`, `broadcastMsg`, `colorize` with `&` color code support

---

## Installation

### JitPack (recommended)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.kolerz:kframework:1.0.0")
}
```

### Maven Local (local dev only)

```bash
./gradlew publishToMavenLocal
```

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("it.kolerz:kotlinframework:1.0.0")
}
```

> Make sure to use `shadowJar` to bundle the framework into your plugin jar, otherwise it won't load on the server.

---

## Usage

### Main class

```kotlin
class MyPlugin : KotlinFramework() {

    override fun onStart() {
        registerCommand("heal", playerOnly = true) {
            player!!.health = 20.0
            reply("&aHealed!")
        }

        registerCommand("broadcast", permission = "myplugin.broadcast") {
            requireArgs(1, "/broadcast <message>") {
                broadcastMsg("&8[&cBroadcast&8] &f${args.joinToString(" ")}")
            }
        }

        registerListener {
            on<PlayerJoinEvent> {
                player.msg("&aWelcome, &f${player.name}&a!")
            }
            on<PlayerDeathEvent>(priority = EventPriority.HIGHEST) {
                deathMessage(null)
            }
        }

        runTimer(0L, 20L * 60L * 5L) {
            broadcastMsg("&eRemember to vote!")
        }
    }
}
```

### Separate command class

```kotlin
class SpawnCommand : KotlinCommand(playerOnly = true) {
    override fun CommandContext.execute() {
        val spawn = getLocation("spawn") ?: run {
            reply("&cSpawn is not set!")
            return
        }
        player!!.teleport(spawn)
        reply("&aTeleported!")
    }
}
```

---

## API

### KotlinFramework

| Method | Description |
|---|---|
| `registerCommand(name, playerOnly, permission) { }` | Inline command |
| `registerCommand(name, executor)` | Class-based command |
| `registerCommands(vararg Pair)` | Multiple commands at once |
| `registerListener { }` | Inline event registration |
| `registerListeners(vararg Listener)` | Multiple listener classes |

### Extensions

| Function | Description |
|---|---|
| `String.colorize()` | Converts `&` to `§` |
| `CommandSender.msg(message)` | Colored message |
| `CommandSender.requirePlayer { }` | Ensures sender is a player |
| `broadcastMsg(message)` | Broadcast to all players |
| `runLater(delay) { }` | Delayed task |
| `runTimer(delay, period) { }` | Repeating task |
| `runAsync { }` | Async task |
| `saveLocation(path, location)` | Save location to config |
| `getLocation(path)` | Read location from config |
| `setConfig(path, value)` | Write and save config value |
| `config(path, default)` | Read config value |

### CommandContext

| Property / Method | Description |
|---|---|
| `sender` | The `CommandSender` |
| `player` | The `Player` (nullable) |
| `args` | Arguments array |
| `reply(message)` | Send colored message to sender |
| `requireArgs(min, usage) { }` | Minimum argument check |

---

## Requirements

- Paper 1.21+
- Java 21+
- Kotlin 2.x

---

## License

MIT
