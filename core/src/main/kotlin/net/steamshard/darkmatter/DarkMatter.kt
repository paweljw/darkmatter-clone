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
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger
import net.steamshard.darkmatter.asset.TextureAsset
import net.steamshard.darkmatter.asset.TextureAtlasAsset
import net.steamshard.darkmatter.audio.AudioService
import net.steamshard.darkmatter.audio.DefaultAudioService
import net.steamshard.darkmatter.ecs.system.*
import net.steamshard.darkmatter.event.GameEventManager
import net.steamshard.darkmatter.screen.BaseScreen
import net.steamshard.darkmatter.screen.GameScreen
import net.steamshard.darkmatter.screen.LoadingScreen

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
    val assets : AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    val audioService: AudioService by lazy { DefaultAudioService(assets) }

    val engine : Engine by lazy {
        PooledEngine().apply {
            val atlas = assets[TextureAtlasAsset.GAME_GRAPHICS.descriptor]
            val backgroundTexture = assets[TextureAsset.BACKGROUDND.descriptor]

            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager, audioService))
            addSystem(DamageSystem(gameEventManager, audioService))
            addSystem(CameraShakeSystem(gameViewport.camera, gameEventManager))
            addSystem(PlayerAnimationSystem(
                defaultRegion = atlas.findRegion("ship_base"),
                leftRegion = atlas.findRegion("ship_left"),
                rightRegion = atlas.findRegion("ship_right"),
                gameEventManager
            ))
            addSystem(AttachSystem())
            addSystem(AnimationSystem(atlas))
            addSystem(RenderSystem(batch, gameViewport, uiViewport, backgroundTexture, gameEventManager))
            addSystem(DebugSystem())
            addSystem(RemovalSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug { "Create game instance" }

        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }

    override fun render() {
        super.render()
        audioService.update()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assets.dispose()
    }
}