package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Game
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import net.steamshard.darkmatter.DarkMatter

abstract class BaseScreen(
    val game: DarkMatter,
    val stage: Stage = game.stage
) : KtxScreen {

    override fun resize(width: Int, height: Int) {
        game.gameViewport.update(width, height, true)
        game.uiViewport.update(width, height, true)
    }
}