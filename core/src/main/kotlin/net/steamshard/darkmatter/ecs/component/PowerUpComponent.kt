package net.steamshard.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class PowerUpType(val animationType: AnimationType) {
    NONE(AnimationType.NONE),
    SPEED_1(AnimationType.SPEED_1),
    SPEED_2(AnimationType.SPEED_2),
    LIFE(AnimationType.LIFE),
    SHIELD(AnimationType.SHIELD),
}

class PowerUpComponent : Component, Pool.Poolable {
    var type = PowerUpType.NONE

    override fun reset() {
        type = PowerUpType.NONE
    }

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}
