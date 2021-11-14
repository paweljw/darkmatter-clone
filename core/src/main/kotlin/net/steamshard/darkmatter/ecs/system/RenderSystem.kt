package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
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
import net.steamshard.darkmatter.ecs.component.FacingDirection
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import net.steamshard.darkmatter.ecs.component.PowerUp
import net.steamshard.darkmatter.ecs.component.TransformComponent
import net.steamshard.darkmatter.event.*
import kotlin.math.min

private val LOG = logger<RenderSystem>()

private const val BACKGROUND_MIN_SCROLLING_SPEED = -0.25f

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    private val backgroundTexture: Texture,
    private val gameEventManager: GameEventManager,
    private val background: Sprite = Sprite(backgroundTexture.apply { setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat) })
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
), GameEventListener {
    private val backgroundScrollingSpeed = Vector2(0f, BACKGROUND_MIN_SCROLLING_SPEED)

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.CollectPowerUp::class, this)
        gameEventManager.addListener(GameEvent.DirectionChange::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(this)
    }

    override fun update(deltaTime: Float) {
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            // render background
            background.run {
                backgroundScrollingSpeed.y = min(
                    BACKGROUND_MIN_SCROLLING_SPEED,
                    backgroundScrollingSpeed.y + deltaTime * .1f
                )
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

    override fun onEvent(event: GameEvent) {
        when(event) {
            is GameEvent.CollectPowerUp -> {
                when(event.type) {
                    is PowerUp.Speed1 -> backgroundScrollingSpeed.y -= .25f
                    is PowerUp.Speed2 -> backgroundScrollingSpeed.y -= .5f
                    else -> Unit
                }
            }
            is GameEvent.DirectionChange -> {
                when(event.direction) {
                    FacingDirection.LEFT -> backgroundScrollingSpeed.x = -.5f
                    FacingDirection.RIGHT -> backgroundScrollingSpeed.x = .5f
                    FacingDirection.DEFAULT -> backgroundScrollingSpeed.x = 0f
                }
            }
            else -> { LOG.error { "Unsupported event passed in: $event" } }
        }

    }
}