package net.steamshard.darkmatter

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.log.logger
import net.steamshard.darkmatter.screen.BaseScreen
import net.steamshard.darkmatter.screen.GameScreen

private val LOG = logger<DarkMatter>()

const val UNIT_SCALE = 1 / 16f

class DarkMatter : KtxGame<BaseScreen>() {
    val batch : Batch by lazy { SpriteBatch(1000) }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        addScreen(GameScreen(this))
        LOG.debug { "Create game instance" }
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}