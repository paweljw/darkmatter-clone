package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import net.steamshard.darkmatter.asset.SoundAsset
import net.steamshard.darkmatter.audio.AudioService
import net.steamshard.darkmatter.ecs.component.*
import net.steamshard.darkmatter.event.GameEvent
import net.steamshard.darkmatter.event.GameEventManager
import kotlin.math.max

const val DAMAGE_AREA_HEIGHT = 2f
private const val DPS = 25f
private const val DEATH_EXPLOSION_DURATION = 0.9f

class DamageSystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) :
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
                audioService.play(SoundAsset.BLOCK)

                if (damage <= 0f) {
                    return
                }
            }

            player.life -= damage
            gameEventManager.dispatchEvent(GameEvent.PlayerHit.apply {
                this.player = entity
                life = player.life
                maxLife = player.maxLife
            })
            audioService.play(SoundAsset.DAMAGE)

            if(player.life <= 0) {
                entity.addComponent<RemoveComponent>(engine) {
                    delay = DEATH_EXPLOSION_DURATION
                }

                entity.addComponent<AnimationComponent>(engine) {
                    stateTime = 0f
                    type = AnimationType.EXPLOSION
                }

                gameEventManager.dispatchEvent(GameEvent.PlayerDeath.apply {
                    distance = player.distance
                })

                audioService.play(SoundAsset.EXPLOSION)
            }
        }
    }
}