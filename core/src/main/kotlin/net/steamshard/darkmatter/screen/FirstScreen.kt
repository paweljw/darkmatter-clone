package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import net.steamshard.darkmatter.DarkMatter
import ktx.log.logger

private val LOG = logger<FirstScreen>()

class FirstScreen(game: DarkMatter) : BaseScreen(game) {
    override fun show() {
        LOG.debug { "First screen shown" }
    }
    override fun render(delta: Float) {
        if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.setScreen<SecondScreen>()
        }
    }
}