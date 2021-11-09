package net.steamshard.darkmatter.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.GdxSet
import net.steamshard.darkmatter.ecs.component.PowerUp
import kotlin.reflect.KClass

// Quite interesting approach with generics:
// https://github.com/Nerachus/eschenberg/commit/d43b7e6c6eee93746605d673760a46142d55113f
// later updated to use pooled events:
// https://github.com/Nerachus/eschenberg/commit/3d8e6806bb615125129627cb71acb4a75a60ca66

sealed class GameEvent {
    object PlayerDeath : GameEvent() {
        var distance = 0f

        override fun toString() = "PlayerDeath(distance=$distance)"
    }

    object CollectPowerUp : GameEvent() {
        lateinit var player: Entity

        var type: PowerUp = PowerUp.None

        override fun toString() = "GameEventCollectPowerUp(type=$type)"
    }

}

interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {
    private val listeners = ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>()

    fun dispatchEvent(event: GameEvent) {
        listeners[event::class]?.forEach { it.onEvent(event) }
    }

    fun addListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = GdxSet()
            listeners.put(type, eventListeners)
        }

        eventListeners.add(listener)
    }

    fun removeListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        listeners[type]?.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values().forEach { it.remove(listener) }
    }
}