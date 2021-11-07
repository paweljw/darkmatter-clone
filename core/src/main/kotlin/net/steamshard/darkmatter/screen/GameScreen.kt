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
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

private val LOG = logger<GameScreen>()

class GameScreen(game: DarkMatter) : BaseScreen(game) {
    private val playerTexture = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val player = engine.entity {
        with<TransformComponent> {
            position.set(0f, 0f, 0f)
        }

        with<GraphicComponent> {
            sprite.run {
                setRegion(playerTexture)
                setSize(texture.width * UNIT_SCALE, texture.height * UNIT_SCALE)
                setOriginCenter()
            }
        }
    }

    override fun show() {
        LOG.debug { "GameScreen shown" }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        playerTexture.dispose()
        game.engine.removeEntity(player)
    }
}