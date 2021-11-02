package net.steamshard.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.log.logger
import net.steamshard.darkmatter.DarkMatter

private val LOG = logger<SecondScreen>()

class SecondScreen(game: DarkMatter) : BaseScreen(game) {
    override fun show() {
        super.show()
        LOG.debug { "Second screen shown" }
    }

    override fun render(delta: Float) {
        if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.setScreen<FirstScreen>()
        }
    }
}