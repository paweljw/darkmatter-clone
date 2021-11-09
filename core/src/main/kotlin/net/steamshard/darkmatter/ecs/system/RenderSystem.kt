package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import net.steamshard.darkmatter.UNIT_SCALE
import net.steamshard.darkmatter.V_HEIGHT
import net.steamshard.darkmatter.V_WIDTH
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

private val LOG = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    private val backgroundTexture: Texture,
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
) {
    private val background = Sprite(backgroundTexture.apply { setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat) })
    private val backgroundScrollingSpeed = Vector2(0f, -0.25f)

    override fun update(deltaTime: Float) {
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            // render background
            background.run {
                scroll(backgroundScrollingSpeed.x * deltaTime, backgroundScrollingSpeed.y * deltaTime)
                draw(it)
            }
        }

        forceSort()

        gameViewport.apply()
        batch.use(gameViewport.camera.combined) {
            // render entities
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| is missing GraphicComponent. entity=$entity" }

        if (graphic.sprite.texture == null) {
            LOG.error { "Entity |entity| has no texture. entity=$entity" }
            return
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(
                transform.interpolatedPosition.x,
                transform.interpolatedPosition.y,
                transform.size.x,
                transform.size.y
            )
            draw(batch)
        }
    }
}