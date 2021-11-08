package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import net.steamshard.darkmatter.DarkMatter
import ktx.log.logger
import ktx.graphics.use
import net.steamshard.darkmatter.UNIT_SCALE
import net.steamshard.darkmatter.V_WIDTH
import net.steamshard.darkmatter.ecs.component.*
import net.steamshard.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import kotlin.math.min

private val LOG = logger<GameScreen>()
private val MAX_DELTA_TIME = 1 / 20f // no less than 20fps

class GameScreen(game: DarkMatter) : BaseScreen(game) {
    private val player = engine.entity {
        with<TransformComponent> {
            setInitialPosition(4.5f, 8f, 0f)
        }

        with<MoveComponent>()
        with<GraphicComponent>()
        with<PlayerComponent>()
        with<FacingComponent>()
    }

    private val darkMatter = engine.entity( {
        with<TransformComponent> {
            setInitialPosition(0f, 0f, 0f)
            size.set(V_WIDTH, DAMAGE_AREA_HEIGHT)
        }
        with<GraphicComponent>()
        with<AnimationComponent> {
            type = AnimationType.DARK_MATTER
        }
    })

    override fun show() {
        LOG.debug { "GameScreen shown" }
    }

    override fun render(delta: Float) {
        engine.update(min(delta, MAX_DELTA_TIME))
    }

    override fun dispose() {
        game.engine.removeEntity(player)
    }
}