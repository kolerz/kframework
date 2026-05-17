package it.kolerz.kframework.listener

import it.kolerz.kframework.KotlinFramework
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

/**
 * A Listener class that allows registering events inline without [org.bukkit.event.EventHandler] annotations.
 *
 * Example usage:
 * ```kotlin
 * registerListener {
 *     on<PlayerJoinEvent> {
 *         player.msg("&aWelcome!")
 *     }
 *     on<PlayerDeathEvent>(priority = EventPriority.HIGH) {
 *         deathMessage(null)
 *     }
 * }
 * ```
 */
class KotlinListener : Listener {

    /** Internal list of registered event handlers. */
    val handlers = mutableListOf<EventHandler<*>>()

    /**
     * Registers an event listener inline.
     *
     * @param T the event type to listen to
     * @param priority the priority at which this listener is called. Defaults to [EventPriority.NORMAL]
     * @param ignoreCancelled if true, the listener is not called for cancelled events. Defaults to false
     * @param action lambda with the event as receiver, called when the event fires
     */
    inline fun <reified T : Event> on(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        noinline action: T.() -> Unit
    ) {
        val handler = EventHandler(T::class.java, priority, ignoreCancelled, action)
        handlers.add(handler)

        val executor = EventExecutor { _, event ->
            if (event is T) action(event)
        }

        KotlinFramework.instance.server.pluginManager.registerEvent(
            T::class.java,
            this,
            priority,
            executor,
            KotlinFramework.instance,
            ignoreCancelled
        )
    }
}

/**
 * Holds metadata about a registered event handler.
 *
 * @param T the event type
 * @property eventClass the class of the event
 * @property priority the priority of the handler
 * @property ignoreCancelled whether cancelled events are ignored
 * @property action the lambda to execute when the event fires
 */
class EventHandler<T : Event>(
    val eventClass: Class<T>,
    val priority: EventPriority,
    val ignoreCancelled: Boolean,
    val action: T.() -> Unit
)

/**
 * Creates and returns a [KotlinListener] with the given event registrations.
 * Useful when you need a reference to the listener to unregister it later.
 *
 * Example:
 * ```kotlin
 * val myListener = listener {
 *     on<PlayerJoinEvent> {
 *         player.msg("&aWelcome!")
 *     }
 * }
 * ```
 *
 * @param block lambda with [KotlinListener] as receiver
 * @return the configured [KotlinListener]
 */
fun listener(block: KotlinListener.() -> Unit): KotlinListener {
    val l = KotlinListener()
    l.block()
    return l
}