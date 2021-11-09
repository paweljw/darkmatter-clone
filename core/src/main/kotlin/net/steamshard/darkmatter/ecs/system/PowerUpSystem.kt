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
import net.steamshard.darkmatter.event.GameEventCollectPowerUp
import net.steamshard.darkmatter.event.GameEventManager
import net.steamshard.darkmatter.event.GameEventType
import kotlin.math.min

private val LOG = logger<PowerUpSystem>()

private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f
private const val POWER_UP_SPEED = -4.75f
private const val BOOST_1_SPEED_GAIN = 3f
private const val BOOST_2_SPEED_GAIN = 3.75f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f

private class SpawnPattern(
    type1: PowerUpType = PowerUpType.NONE,
    type2: PowerUpType = PowerUpType.NONE,
    type3: PowerUpType = PowerUpType.NONE,
    type4: PowerUpType = PowerUpType.NONE,
    type5: PowerUpType = PowerUpType.NONE,
    val types: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5)
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
        SpawnPattern(type1 = PowerUpType.SPEED_1, type2 = PowerUpType.SPEED_2, type5 = PowerUpType.LIFE),
        SpawnPattern(type2 = PowerUpType.LIFE, type3 = PowerUpType.SHIELD, type4 = PowerUpType.SPEED_2)
    )
    private val currentSpawnPattern = GdxArray<PowerUpType>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime <= 0f) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)

            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, 1)].types)
            }

            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType == PowerUpType.NONE) {
                return
            }

            spawnPowerUp(powerUpType, MathUtils.random(0f, V_WIDTH))
        }
    }

    private fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float = V_HEIGHT) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(x, y, 0f)
            }
            with<GraphicComponent>()
            with<AnimationComponent> {
                type = powerUpType.animationType
            }
            with<PowerUpComponent> {
                type = powerUpType
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

        when (powerUpComponent.type) {
            PowerUpType.SPEED_1 -> player[MoveComponent.mapper]?.let { it.speed.y += BOOST_1_SPEED_GAIN }
            PowerUpType.SPEED_2 -> player[MoveComponent.mapper]?.let { it.speed.y += BOOST_2_SPEED_GAIN }
            PowerUpType.LIFE -> player[PlayerComponent.mapper]?.let { it.life = min(it.life + LIFE_GAIN, MAX_LIFE) }
            PowerUpType.SHIELD -> player[PlayerComponent.mapper]?.let {
                it.shield = min(it.shield + SHIELD_GAIN, MAX_SHIELD)
            }
            else -> LOG.error { "PowerUpComponent without a type in |entity|. entity=$powerUp" }
        }

        powerUp.addComponent<RemoveComponent>(engine)

        gameEventManager.dispatchEvent(
            GameEventType.COLLECT_POWER_UP,
            GameEventCollectPowerUp.apply {
                this.player = player
                type = powerUpComponent.type
            }
        )
    }
}