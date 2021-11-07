package net.steamshard.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.log.logger
import net.steamshard.darkmatter.ecs.system.RenderSystem
import net.steamshard.darkmatter.screen.BaseScreen
import net.steamshard.darkmatter.screen.GameScreen

private val LOG = logger<DarkMatter>()

const val UNIT_SCALE = 1 / 16f

class DarkMatter : KtxGame<BaseScreen>() {
    val batch : Batch by lazy { SpriteBatch(1000) }
    val gameViewport = FitViewport(9f, 16f)

    val engine : Engine by lazy {
        PooledEngine().apply {
            addSystem(RenderSystem(batch, gameViewport))
        }
    }

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