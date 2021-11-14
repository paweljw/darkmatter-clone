package net.steamshard.darkmatter.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
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

enum class SoundAsset(
    fileName: String,
    directory: String = "sound",
    val descriptor: AssetDescriptor<Sound> = AssetDescriptor("$directory/$fileName", Sound::class.java)
) {
    BOOST_1("boost1.wav"),
    BOOST_2("boost2.wav"),
    LIFE("life.wav"),
    SHIELD("shield.wav"),
    DAMAGE("damage.wav"),
    EXPLOSION("explosion.wav"),
    SPAWN("spawn.wav"),
    BLOCK("block.wav"),
}

enum class MusicAsset(
    fileName: String,
    directory: String = "music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$fileName", Music::class.java)
) {
    GAME("game.mp3"),
    GAME_OVER("game_over.mp3"),
    MENU("menu.mp3"),
}