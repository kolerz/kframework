package it.kolerz.kframework.extensions

import it.kolerz.kframework.KotlinFramework
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Saves a [Location] to the plugin's config.yml under the given path.
 * Automatically saves the config after writing.
 *
 * Example:
 * ```kotlin
 * saveLocation("spawn", player.location)
 * // config.yml:
 * // spawn:
 * //   world: world
 * //   x: 100.0
 * //   y: 64.0
 * //   z: 200.0
 * //   yaw: 90.0
 * //   pitch: 0.0
 * ```
 *
 * @param path the config path to save the location under
 * @param location the [Location] to save
 */
fun saveLocation(path: String, location: Location) {
    val config = KotlinFramework.instance.config
    config.set("$path.world", location.world.name)
    config.set("$path.x", location.x)
    config.set("$path.y", location.y)
    config.set("$path.z", location.z)
    config.set("$path.yaw", location.yaw.toDouble())
    config.set("$path.pitch", location.pitch.toDouble())
    KotlinFramework.instance.saveConfig()
}

/**
 * Reads a [Location] from the plugin's config.yml at the given path.
 * Returns null if the path doesn't exist or the world is not loaded.
 *
 * Example:
 * ```kotlin
 * val spawn = getLocation("spawn") ?: return
 * player.teleport(spawn)
 * ```
 *
 * @param path the config path to read the location from
 * @return the [Location] if found, null otherwise
 */
fun getLocation(path: String): Location? {
    val config = KotlinFramework.instance.config
    val worldName = config.getString("$path.world") ?: run {
        KotlinFramework.instance.logger.warning("[$path] world not found in config!")
        return null
    }
    val world = Bukkit.getWorld(worldName) ?: run {
        KotlinFramework.instance.logger.warning("World '$worldName' is not loaded!")
        return null
    }
    return Location(
        world,
        config.getDouble("$path.x"),
        config.getDouble("$path.y"),
        config.getDouble("$path.z"),
        config.getDouble("$path.yaw").toFloat(),
        config.getDouble("$path.pitch").toFloat()
    )
}

/**
 * Gets a value from the plugin's config.yml at the given path.
 * Returns the default value if the path doesn't exist.
 *
 * Example:
 * ```kotlin
 * val maxPlayers = config("settings.max-players", 16)
 * ```
 *
 * @param path the config path to read
 * @param default the default value if the path doesn't exist
 * @return the value at the path, or the default
 */
fun config(path: String, default: Any? = null) =
    KotlinFramework.instance.config.get(path, default)

/**
 * Sets a value in the plugin's config.yml and saves it immediately.
 *
 * Example:
 * ```kotlin
 * setConfig("settings.max-players", 32)
 * ```
 *
 * @param path the config path to write
 * @param value the value to set
 */
fun setConfig(path: String, value: Any?) {
    KotlinFramework.instance.config.set(path, value)
    KotlinFramework.instance.saveConfig()
}