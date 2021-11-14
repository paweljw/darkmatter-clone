package net.steamshard.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.log.logger
import net.steamshard.darkmatter.ecs.system.*
import net.steamshard.darkmatter.event.GameEventManager
import net.steamshard.darkmatter.screen.BaseScreen
import net.steamshard.darkmatter.screen.GameScreen

private val LOG = logger<DarkMatter>()

const val UNIT_SCALE = 1 / 16f
const val V_WIDTH = 9f
const val V_HEIGHT = 16f

const val V_WIDTH_PIXELS = 135f
const val V_HEIGHT_PIXELS = 240f

class DarkMatter : KtxGame<BaseScreen>() {
    val batch : Batch by lazy { SpriteBatch(1000) }
    val uiViewport = FitViewport(V_WIDTH_PIXELS, V_HEIGHT_PIXELS)
    val gameViewport = FitViewport(V_WIDTH, V_HEIGHT)
    val gameEventManager = GameEventManager()

    private val graphicsAtlas by lazy { TextureAtlas(Gdx.files.internal("darkmatter.atlas")) }
    private val backgroundTexture by lazy { Texture(Gdx.files.internal("background.png")) }

    val engine : Engine by lazy {
        PooledEngine().apply {
            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager))
            addSystem(DamageSystem(gameEventManager))
            addSystem(CameraShakeSystem(gameViewport.camera, gameEventManager))
            addSystem(PlayerAnimationSystem(
                defaultRegion = graphicsAtlas.findRegion("ship_base"),
                leftRegion = graphicsAtlas.findRegion("ship_left"),
                rightRegion = graphicsAtlas.findRegion("ship_right"),
            ))
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(RenderSystem(batch, gameViewport, uiViewport, backgroundTexture, gameEventManager))
            addSystem(DebugSystem())
            addSystem(RemovalSystem())
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
        graphicsAtlas.dispose()
    }
}