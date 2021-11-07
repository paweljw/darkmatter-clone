package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import net.steamshard.darkmatter.ecs.component.PlayerComponent
import net.steamshard.darkmatter.ecs.component.RemoveComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent
import kotlin.math.max

private const val DAMAGE_AREA_HEIGHT = 2f
private const val DPS = 25f
private const val DEATH_EXPLOSION_DURATION = 0.9f

class DamageSystem :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| is missing PlayerComponent. entity=$entity" }

        if(transform.position.y <= DAMAGE_AREA_HEIGHT) {
            var damage = DPS * deltaTime

            if(player.shield > 0f) {
                val blockAmount = player.shield
                player.shield = max(0f, player.shield - damage)
                damage -= blockAmount

                if (damage <= 0f) {
                    return
                }
            }

            player.life -= damage

            if(player.life <= 0) {
                entity.addComponent<RemoveComponent>(engine) {
                    delay = DEATH_EXPLOSION_DURATION
                }
            }
        }
    }
}