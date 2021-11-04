package net.steamshard.darkmatter.screen

import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen
import net.steamshard.darkmatter.DarkMatter

abstract class BaseScreen(val game: DarkMatter, val batch: Batch = game.batch) : KtxScreen