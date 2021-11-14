package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.stack
import net.steamshard.darkmatter.DarkMatter
import net.steamshard.darkmatter.asset.*

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: DarkMatter) : BaseScreen(game) {
    private lateinit var progressBar: Image
    private lateinit var touchLabel: Label

    override fun show() {
        val before = System.currentTimeMillis()

        game.audioService.play(MusicAsset.MENU)

        val assetRefs = gdxArrayOf(
            ShaderProgramAsset.values().map { game.assets.loadAsync(it.descriptor) },
            TextureAsset.values().map { game.assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { game.assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { game.assets.loadAsync(it.descriptor) },
        ).flatten()

        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "Assets loaded in ${System.currentTimeMillis() - before}ms" }
            assetsLoaded()
        }

        setupUI()
    }

    private fun setupUI() {
        stage.actors {
            table {
                defaults().fillX().expandX()

                label("Wengosh", "gradient") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                row()

                label("Touch To Begin", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                    color.a = 0f
                }
                row()

                stack { cell ->
                    progressBar = image("life_bar").apply {
                        scaleX = 0f
                    }
                    touchLabel = label("Loading...", "default") {
                        setAlignment(Align.center)
                    }
                    cell.padLeft(5f).padRight(5f)
                }

                setFillParent(true)
                pack()
            }
        }

        // This comes in handy
//        stage.isDebugAll = true
    }

    override fun render(delta: Float) {
        if (game.assets.progress.isFinished && Gdx.input.justTouched() && game.containsScreen<GameScreen>()) {
            game.setScreen<GameScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }

        progressBar.scaleX = game.assets.progress.percent
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    override fun hide() {
        stage.clear()
        super.hide()
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.addScreen(GameOverScreen(game))

        touchLabel += forever(
            sequence(
                fadeIn(.5f) + fadeOut(.5f)
            )
        )
        touchLabel.setText("Touch to wengosz")
    }
}
