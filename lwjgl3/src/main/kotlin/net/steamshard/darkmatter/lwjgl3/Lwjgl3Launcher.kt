package net.steamshard.darkmatter.lwjgl3

import net.steamshard.darkmatter.DarkMatter

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    Lwjgl3Application(DarkMatter(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("DarkMatter")

        useVsync(true)
        setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)

        setWindowedMode(640, 480)
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    })
}
