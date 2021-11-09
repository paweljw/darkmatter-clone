package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.*
import net.steamshard.darkmatter.ecs.component.AttachComponent
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import net.steamshard.darkmatter.ecs.component.RemoveComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

class AttachSystem : EntityListener,
    IteratingSystem(
        allOf(AttachComponent::class, TransformComponent::class, GraphicComponent::class).exclude(
            RemoveComponent::class
        ).get()
    ) {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        entities.forEach { entity ->
            entity[AttachComponent.mapper]?.let { attach ->
                if (attach.entity == removedEntity) {
                    entity.addComponent<RemoveComponent>(engine)
                }
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attach = entity[AttachComponent.mapper]
        require(attach != null) { "Entity |entity| is missing AttachComponent. entity=$entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| is missing GraphicComponent. entity=$entity" }

        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        if (attach.entity.has(RemoveComponent.mapper)) {
            graphic.sprite.setAlpha(0f)
            return
        }

        attach.entity[TransformComponent.mapper]?.let { attachTransform ->
            transform.interpolatedPosition.set(
                attachTransform.interpolatedPosition.x + attach.offset.x,
                attachTransform.interpolatedPosition.y + attach.offset.y,
                transform.interpolatedPosition.z
            )
        }

        attach.entity[GraphicComponent.mapper]?.let { attachGraphic ->
            graphic.sprite.setAlpha(attachGraphic.sprite.color.a)
        }
    }
}