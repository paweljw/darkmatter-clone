package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

val LOG = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
) {
    override fun update(deltaTime: Float) {
        forceSort()
        gameViewport.apply()
        batch.use(gameViewport.camera.combined) {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| is missing GraphicComponent. entity=$entity" }

        if(graphic.sprite.texture == null) {
            LOG.error {"Entity |entity| has no texture. entity=$entity"}
            return
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
            draw(batch)
        }
    }
}