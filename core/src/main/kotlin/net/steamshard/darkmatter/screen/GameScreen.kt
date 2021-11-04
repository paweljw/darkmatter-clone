package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import net.steamshard.darkmatter.DarkMatter
import ktx.log.logger
import ktx.graphics.use

private val LOG = logger<GameScreen>()

class GameScreen(game: DarkMatter) : BaseScreen(game) {
    private val viewport = FitViewport(9f, 16f)
    private val texture = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val sprite = Sprite(texture).apply {
        setSize(1f, 1f)
    }
    private val speed = 0.1f

    override fun show() {
        LOG.debug { "Screen shown" }
        sprite.setPosition(1f, 1f)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        viewport.apply()

        batch.use(viewport.camera.combined) {
            sprite.draw(it)
        }

        var positionX = sprite.x
        var positionY = sprite.y

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            positionY += speed
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            positionY -= speed
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            positionX -= speed
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            positionX += speed
        }

        positionX += 9f
        positionY += 16f

        positionX %= 9f
        positionY %= 16f

        sprite.setPosition(positionX, positionY)
    }

    override fun dispose() {
        texture.dispose()
    }
}