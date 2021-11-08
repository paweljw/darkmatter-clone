package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import net.steamshard.darkmatter.ecs.component.Animation2D
import net.steamshard.darkmatter.ecs.component.AnimationComponent
import net.steamshard.darkmatter.ecs.component.AnimationType
import net.steamshard.darkmatter.ecs.component.GraphicComponent
import java.util.*

private val LOG = logger<AnimationSystem>()

class AnimationSystem(
    private val atlas: TextureAtlas
): IteratingSystem(allOf(AnimationComponent::class, GraphicComponent::class).get()), EntityListener {
    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        engine.removeEntityListener(this)
        super.removedFromEngine(engine)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = entity[AnimationComponent.mapper]
        require(animationComponent != null) { "Entity |entity| is missing AnimationComponent. entity=$entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| is missing GraphicComponent. entity=$entity" }

        if(animationComponent.type == AnimationType.NONE) {
            LOG.error { "No type specified for animation component $animationComponent for |entity| $entity. "}
            return
        }

        if(animationComponent.type == animationComponent.animation.type) {
            animationComponent.stateTime += deltaTime
        } else {
            animationComponent.stateTime = 0f
            animationComponent.animation = getAnimation(animationComponent.type)
        }

        val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
        graphic.setSpriteRegion(frame)
    }

    override fun entityAdded(entity: Entity) {
        entity[AnimationComponent.mapper]?.let { animationComponent ->
            animationComponent.animation = getAnimation(animationComponent.type)
            val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
            entity[GraphicComponent.mapper]?.setSpriteRegion(frame)
        }
    }

    private fun getAnimation(type: AnimationType): Animation2D {
        var animation = animationCache[type]

        if(animation == null) {
            var regions = atlas.findRegions(type.atlasKey)
            if(regions.isEmpty) {
                LOG.error { "Cannot find animations for atlasKey ${type.atlasKey}" }
                regions = atlas.findRegions("error")

                if(regions.isEmpty) throw GdxRuntimeException("Even error is missing, WTF")
            }
            animation = Animation2D(type, regions, type.playMode, type.speedRate)
            animationCache[type] = animation
        }

        return animation
    }

    override fun entityRemoved(entity: Entity) = Unit
}