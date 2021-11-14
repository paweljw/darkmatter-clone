package net.steamshard.darkmatter.screen

import com.badlogic.ashley.core.Engine
import ktx.ashley.entity
import ktx.ashley.with
import net.steamshard.darkmatter.DarkMatter
import ktx.log.logger
import net.steamshard.darkmatter.UNIT_SCALE
import net.steamshard.darkmatter.V_WIDTH
import net.steamshard.darkmatter.asset.MusicAsset
import net.steamshard.darkmatter.ecs.component.*
import net.steamshard.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import net.steamshard.darkmatter.event.GameEvent
import net.steamshard.darkmatter.event.GameEventListener
import kotlin.math.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f // no less than 20fps

class GameScreen(game: DarkMatter, private val engine: Engine = game.engine) : BaseScreen(game), GameEventListener {
    override fun show() {
        LOG.debug { "GameScreen shown" }

        game.gameEventManager.addListener(GameEvent.PlayerDeath::class, this)

        spawnPlayer()

        game.audioService.play(MusicAsset.GAME)

        engine.entity {
            with<TransformComponent> {
                setInitialPosition(0f, 0f, 0f)
                size.set(V_WIDTH, DAMAGE_AREA_HEIGHT)
            }
            with<GraphicComponent>()
            with<AnimationComponent> {
                type = AnimationType.DARK_MATTER
            }
        }
    }

    override fun hide() {
        super.hide()
        game.gameEventManager.removeListener(this)
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayerDeath -> {
                LOG.debug { "Player death travelled: ${event.distance}" }

                spawnPlayer()
            }
            else -> {
                LOG.error { "Unsupported event $event" }
            }
        }
    }

    private fun spawnPlayer() {
        val player = engine.entity {
            with<TransformComponent> {
                setInitialPosition(4.5f, 8f, -1f)
            }

            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }

        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = player
                offset.set(0f * UNIT_SCALE, -8f * UNIT_SCALE)
            }
            with<GraphicComponent>()
            with<AnimationComponent> {
                type = AnimationType.FIRE
            }
        }
    }

    override fun render(delta: Float) {
        engine.update(min(delta, MAX_DELTA_TIME))
    }
}