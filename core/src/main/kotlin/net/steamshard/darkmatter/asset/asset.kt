package net.steamshard.darkmatter.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

enum class TextureAsset(
    fileName: String,
    val descriptor: AssetDescriptor<Texture> = AssetDescriptor("$fileName", Texture::class.java)
) {
    BACKGROUDND("background.png")
}

enum class TextureAtlasAsset(
    fileName: String,
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$fileName", TextureAtlas::class.java)
) {
    GAME_GRAPHICS("darkmatter.atlas")
}