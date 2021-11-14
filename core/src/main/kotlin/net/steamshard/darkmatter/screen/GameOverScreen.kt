package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.utils.Align
import ktx.actors.plus
import ktx.preferences.get
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import net.steamshard.darkmatter.DarkMatter
import net.steamshard.darkmatter.asset.MusicAsset
import kotlin.math.roundToInt

class GameOverScreen(game: DarkMatter) : BaseScreen(game) {

    override fun show() {
        game.audioService.play(MusicAsset.GAME_OVER, loop = false)

        stage.actors {
            table {
                defaults().fillX().expandX()

                label("U dieded >:(", "gradient") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                row().padTop(10f)

                label("Last Score: ${game.preferences.get<Float>("lastScore", 0f).roundToInt()}", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                row()

                label("High Score: ${game.preferences.get<Float>("highScore", 0f).roundToInt()}", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                row().padTop(30f)
                label("Touch to wengosz again", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                    addAction(
                        forever(
                            sequence(
                                fadeIn(.5f) + fadeOut(.5f)
                            )
                        )
                    )
                }


                setFillParent(true)
                pack()
            }
        }
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) {
            game.setScreen<GameScreen>()
        }

        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    override fun hide() {
        super.hide()
        stage.clear()
    }
}