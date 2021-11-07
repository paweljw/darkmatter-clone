package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get
import net.steamshard.darkmatter.ecs.component.PlayerComponent
import net.steamshard.darkmatter.ecs.component.TransformComponent

private const val WINDOW_INFO_UPDATE_RATE = 0.25f

class DebugSystem : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_INFO_UPDATE_RATE) {
    init {
        setProcessing(true) // TODO: set to false for release
    }

    override fun processEntity(entity: Entity) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| is missing TransformComponent. entity=$entity" }

        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| is missing PlayerComponent. entity=$entity" }

        when {
            Gdx.input.isKeyPressed(Input.Keys.NUM_1) -> { // kill player
                transform.position.y = 1f
                player.life = 1f
                player.shield = 0f
            }

            Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> { // lift player
                transform.position.y = 15f
                player.life = 100f
                player.shield = 100f
            }
        }

        Gdx.graphics.setTitle("DM debug: lf:${player.life} sh${player.shield} pos:${transform.position} ")
    }
}