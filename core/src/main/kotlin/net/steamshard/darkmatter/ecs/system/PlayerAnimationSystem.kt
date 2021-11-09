package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import net.steamshard.darkmatter.ecs.component.*

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
) : IteratingSystem(
    allOf(PlayerComponent::class, FacingComponent::class, GraphicComponent::class).exclude(RemoveComponent::class).get()
), EntityListener {
    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        require(facing != null) { "Entity |entity| is missing FacingComponent. entity=$entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| is missing GraphicComponent. entity=$entity" }

        if (facing.direction == facing.lastDirection && graphic.sprite.texture != null) {
            // texture set, direction fine, noop
            return
        }

        val region =  when(facing.direction) {
            FacingDirection.LEFT -> leftRegion
            FacingDirection.RIGHT -> rightRegion
            FacingDirection.DEFAULT -> defaultRegion
        }

        graphic.setSpriteRegion(region)
    }

    override fun entityAdded(entity: Entity) {
        entity[GraphicComponent.mapper]?.setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity?) = Unit

}