package net.steamshard.darkmatter.screen

import kotlinx.coroutines.launch
import kotlinx.coroutines.joinAll
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.logger
import net.steamshard.darkmatter.DarkMatter
import net.steamshard.darkmatter.asset.MusicAsset
import net.steamshard.darkmatter.asset.SoundAsset
import net.steamshard.darkmatter.asset.TextureAsset
import net.steamshard.darkmatter.asset.TextureAtlasAsset

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: DarkMatter) : BaseScreen(game) {
    override fun show() {
        val before = System.currentTimeMillis()

        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { game.assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { game.assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { game.assets.loadAsync(it.descriptor) },
        ).flatten()

        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "Assets loaded in ${System.currentTimeMillis() - before}ms" }
            assetsLoaded()
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        dispose()
    }
}
