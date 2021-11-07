package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import net.steamshard.darkmatter.ecs.component.FacingComponent
import net.steamshard.darkmatter.ecs.component.FacingDirection
import net.steamshard.darkmatter.ecs.component.PlayerComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

private const val TOUCH_TOLERANCE_DISTANCE = .2f

class PlayerInputSystem(
    private val gameViewport: Viewport
) : IteratingSystem(
    allOf(PlayerComponent::class, FacingComponent::class, TransformComponent::class).get()
) {
    private val tmpVec = Vector2(0f, 0f) // done like this to limit allocations

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        require(facing != null) { "Entity |entity| is missing FacingComponent. entity=$entity" }

        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        // We take the x coordinate from input (last touch or mouse position)
        // and translate it to world coordinates with `unproject`
        // might generate issues in real world since input.x/y is originated in top left corner
        tmpVec.x = Gdx.input.x.toFloat()
        gameViewport.unproject(tmpVec)

        val diffX = tmpVec.x - transform.position.x - transform.size.x * .5f

        facing.lastDirection = facing.direction

        facing.direction = when {
            diffX < -TOUCH_TOLERANCE_DISTANCE -> FacingDirection.LEFT
            diffX > TOUCH_TOLERANCE_DISTANCE -> FacingDirection.RIGHT
            else -> FacingDirection.DEFAULT
        }
    }
}