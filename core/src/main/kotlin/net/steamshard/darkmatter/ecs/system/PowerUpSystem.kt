package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import ktx.ashley.*
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.logger
import net.steamshard.darkmatter.V_HEIGHT
import net.steamshard.darkmatter.V_WIDTH
import net.steamshard.darkmatter.ecs.component.*
import net.steamshard.darkmatter.event.GameEvent
import net.steamshard.darkmatter.event.GameEventManager
import kotlin.math.min

private val LOG = logger<PowerUpSystem>()

private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f
private const val POWER_UP_SPEED = -4.75f

private class SpawnPattern(
    type1: PowerUp = PowerUp.None,
    type2: PowerUp = PowerUp.None,
    type3: PowerUp = PowerUp.None,
    type4: PowerUp = PowerUp.None,
    type5: PowerUp = PowerUp.None,
    val types: GdxArray<PowerUp> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class PowerUpSystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(
    allOf(PowerUpComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()
) {
    private val playerBoundingRect = Rectangle()
    private val powerUpBoundingRect = Rectangle()
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())
    }
    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(type1 = PowerUp.Speed1, type2 = PowerUp.Speed2, type5 = PowerUp.Life),
        SpawnPattern(type2 = PowerUp.Life, type3 = PowerUp.Shield, type4 = PowerUp.Speed2)
    )
    private val currentSpawnPattern = GdxArray<PowerUp>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime <= 0f) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)

            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, 1)].types)
            }

            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType == PowerUp.None) {
                return
            }

            spawnPowerUp(powerUpType, MathUtils.random(0f, V_WIDTH))
        }
    }

    private fun spawnPowerUp(powerUp: PowerUp, x: Float, y: Float = V_HEIGHT) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(x, y, 0f)
            }
            with<GraphicComponent>()
            with<AnimationComponent> {
                type = powerUp.animationType
            }
            with<PowerUpComponent> {
                type = powerUp
            }
            with<MoveComponent> {
                speed.y = POWER_UP_SPEED
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        if (transform.position.y <= 1f) {
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        powerUpBoundingRect.set(
            transform.position.x,
            transform.position.y,
            transform.size.x,
            transform.size.y,
        )
        playerEntities.forEach { playerEntity ->
            playerEntity[TransformComponent.mapper]?.let { playerTransform ->
                playerBoundingRect.set(
                    playerTransform.position.x,
                    playerTransform.position.y,
                    playerTransform.size.x,
                    playerTransform.size.y,
                )

                if (playerBoundingRect.overlaps(powerUpBoundingRect)) {
                    collectPowerUp(playerEntity, entity)
                }
            }
        }
    }

    private fun collectPowerUp(player: Entity, powerUp: Entity) {
        val powerUpComponent = powerUp[PowerUpComponent.mapper]
        require(powerUpComponent != null) { "Entity |entity| is missing PowerUpComponent. entity=$powerUp" }

        if(powerUpComponent.type is PowerUp.None) {
            LOG.error { "PowerUpComponent without a type in |entity|. entity=$powerUp" }
            return
        }

        powerUpComponent.type.applyEffect(player)

        powerUp.addComponent<RemoveComponent>(engine)

        gameEventManager.dispatchEvent(
            GameEvent.CollectPowerUp.apply {
                this.player = player
                this.type = powerUpComponent.type
            }
        )
    }
}