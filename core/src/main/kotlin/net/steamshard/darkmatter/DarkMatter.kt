package net.steamshard.darkmatter

import com.badlogic.gdx.Game

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class DarkMatter : Game() {
    override fun create() {
        setScreen(FirstScreen())
    }
}