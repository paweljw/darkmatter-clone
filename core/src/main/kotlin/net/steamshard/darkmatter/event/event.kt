package net.steamshard.darkmatter.event

import com.badlogic.ashley.core.Entity
import ktx.collections.GdxSet
import net.steamshard.darkmatter.ecs.component.PowerUpType
import java.util.*

enum class GameEventType {
    PLAYER_DEATH,
    COLLECT_POWER_UP
}

interface GameEvent

object GameEventPlayerDeath : GameEvent {
    var distance = 0f

    override fun toString() = "GameEventPlayerDeath(distance=$distance)"
}

object GameEventCollectPowerUp : GameEvent {
    lateinit var player: Entity

    var type = PowerUpType.NONE

    override fun toString() = "GameEventCollectPowerUp(type=$type)"
}

interface GameEventListener {
    fun onEvent(type: GameEventType, data: GameEvent? = null)
}

class GameEventManager {
    private val listeners = EnumMap<GameEventType, GdxSet<GameEventListener>>(GameEventType::class.java)

    fun dispatchEvent(type: GameEventType, data: GameEvent? = null) {
        listeners[type]?.forEach { it.onEvent(type, data) }
    }

    fun addListener(type: GameEventType, listener: GameEventListener) {
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = GdxSet<GameEventListener>()
            listeners[type] = eventListeners
        }

        eventListeners.add(listener)
    }

    fun removeListener(type: GameEventType, listener: GameEventListener) {
        listeners[type]?.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values. forEach { it.remove(listener) }
    }
}