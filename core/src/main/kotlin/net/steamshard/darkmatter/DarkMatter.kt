package net.steamshard.darkmatter

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.logger
import net.steamshard.darkmatter.screen.FirstScreen
import net.steamshard.darkmatter.screen.SecondScreen

private val LOG = logger<DarkMatter>()

class DarkMatter : KtxGame<KtxScreen>() {
    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        addScreen(FirstScreen(this))
        addScreen(SecondScreen(this))
        LOG.debug { "Create game instance" }
        setScreen<FirstScreen>()
    }
}