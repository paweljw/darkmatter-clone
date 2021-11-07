package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import net.steamshard.darkmatter.ecs.component.RemoveComponent

class RemovalSystem : IteratingSystem(allOf(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val remove = entity[RemoveComponent.mapper]
        require(remove != null) { "Entity |entity| is missing RemoveComponent. entity=$entity" }

        remove.delay -= deltaTime

        if (remove.delay <= 0f) {
            engine.removeEntity(entity)
        }
    }
}