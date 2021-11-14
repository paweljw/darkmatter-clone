package net.steamshard.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen
import net.steamshard.darkmatter.DarkMatter

abstract class BaseScreen(
    val game: DarkMatter,
) : KtxScreen {

    override fun resize(width: Int, height: Int) {
        game.gameViewport.update(width, height, true)
        game.uiViewport.update(width, height, true)
    }
}