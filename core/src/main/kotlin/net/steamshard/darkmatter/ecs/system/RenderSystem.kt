package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import net.steamshard.darkmatter.asset.ShaderProgramAsset
import net.steamshard.darkmatter.ecs.component.*
import net.steamshard.darkmatter.event.GameEvent
import net.steamshard.darkmatter.event.GameEventListener
import net.steamshard.darkmatter.event.GameEventManager
import kotlin.math.min

private val LOG = logger<RenderSystem>()

private const val BACKGROUND_MIN_SCROLLING_SPEED = -0.25f

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    private val backgroundTexture: Texture,
    private val gameEventManager: GameEventManager,
    private val background: Sprite = Sprite(backgroundTexture.apply { setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat) }),
    private val outlineShader: ShaderProgram
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
), GameEventListener {
    private val backgroundScrollingSpeed = Vector2(0f, BACKGROUND_MIN_SCROLLING_SPEED)
    private val textureSizeLoc = outlineShader.getUniformLocation("u_textureSize")
    private val outlineColorLoc = outlineShader.getUniformLocation("u_outlineColor")
    private val outlineColor = Color(0f, 113f/255f, 214f/255f, 1f)
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())

    }

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

        // render outline
        renderEntityOutlines()
    }

    private fun renderEntityOutlines() {
        batch.use(gameViewport.camera.combined) {
            it.shader = outlineShader
            playerEntities.forEach { entity ->
                renderPlayerOutlines(entity, it)
            }
            it.shader = null
        }
    }

    private fun renderPlayerOutlines(entity: Entity, batch: Batch) {
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| is missing PlayerComponent. entity=$entity" }

        if(player.shield > 0f) {
            outlineColor.a = MathUtils.clamp(player.shield / player.maxShield, 0f, 1f) * 0.5f + 0.5f
            outlineShader.setUniformf(outlineColorLoc, outlineColor)
            entity[GraphicComponent.mapper]?.let { graphicComponent ->
                graphicComponent.sprite.run {
                    outlineShader.setUniformf(textureSizeLoc, texture.width.toFloat(), texture.height.toFloat())
                    draw(batch)
                }
            }
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