package net.steamshard.darkmatter.ui

import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin
import net.steamshard.darkmatter.asset.BitmapFontAsset
import net.steamshard.darkmatter.asset.TextureAtlasAsset

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.SKIN.descriptor]
    val gradientFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val normalFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]

    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label("default") {
            font = normalFont
        }
        label("gradient") {
            font = gradientFont
        }
    }
}