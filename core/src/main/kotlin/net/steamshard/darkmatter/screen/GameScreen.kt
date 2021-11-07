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
import net.steamshard.darkmatter.ecs.component.FacingComponent
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import net.steamshard.darkmatter.ecs.component.PlayerComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

private val LOG = logger<GameScreen>()

class GameScreen(game: DarkMatter) : BaseScreen(game) {
    private val player = engine.entity {
        with<TransformComponent> {
            position.set(5f, 3f, 0f)
        }

        with<GraphicComponent>()
        with<PlayerComponent>()
        with<FacingComponent>()
    }

    override fun show() {
        LOG.debug { "GameScreen shown" }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        game.engine.removeEntity(player)
    }
}