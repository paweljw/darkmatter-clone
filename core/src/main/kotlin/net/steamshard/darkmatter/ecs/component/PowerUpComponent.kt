package net.steamshard.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor
import net.steamshard.darkmatter.asset.SoundAsset
import net.steamshard.darkmatter.audio.AudioService
import kotlin.math.min

sealed class PowerUp(
    val lifeGain: Float = 0f,
    val shieldGain: Float = 0f,
    val speedGain: Float = 0f,
    val animationType: AnimationType = AnimationType.NONE,
    val soundAsset: SoundAsset = SoundAsset.BLOCK
) {
    fun applyEffect(entity: Entity) {
        entity[PlayerComponent.mapper]?.let {
            it.life = min(it.life + lifeGain, MAX_LIFE)
            it.shield = min(it.shield + shieldGain, MAX_SHIELD)
        }
        entity[MoveComponent.mapper]?.let {
            it.speed.y += speedGain
        }
    }

    fun playSound(audioService: AudioService) {
        audioService.play(soundAsset)
    }

    object None: PowerUp()
    object Speed1: PowerUp(speedGain = 3f, animationType = AnimationType.SPEED_1, soundAsset = SoundAsset.BOOST_1)
    object Speed2: PowerUp(speedGain = 3.75f, animationType = AnimationType.SPEED_2, soundAsset = SoundAsset.BOOST_2)
    object Life: PowerUp(lifeGain = 25f, animationType = AnimationType.LIFE, soundAsset = SoundAsset.LIFE)
    object Shield: PowerUp(shieldGain = 25f, animationType = AnimationType.SHIELD, soundAsset = SoundAsset.SHIELD)
}

class PowerUpComponent : Component, Pool.Poolable {
    var type: PowerUp = PowerUp.None

    override fun reset() {
        type = PowerUp.None
    }

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}
