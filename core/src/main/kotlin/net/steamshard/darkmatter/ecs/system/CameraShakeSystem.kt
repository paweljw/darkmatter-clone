package net.steamshard.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.collections.GdxArray
import net.steamshard.darkmatter.event.GameEvent
import net.steamshard.darkmatter.event.GameEventListener
import net.steamshard.darkmatter.event.GameEventManager

private class CameraShake : Pool.Poolable {
    var maxDistortion = 0f
    var duration = 0f
    lateinit var camera: Camera
    private var storeCameraPos = true
    private val origCameraPos = Vector3()
    private var currentDuration = 0f

    fun update(deltaTime: Float) : Boolean {
        if(storeCameraPos) {
            storeCameraPos = false
            origCameraPos.set(camera.position)
        }

        if (currentDuration < duration) {
            val currentPower = maxDistortion * ((duration-currentDuration)/duration)
            camera.position.x = origCameraPos.x + MathUtils.random(-1f, 1f) * currentPower
            camera.position.y = origCameraPos.y + MathUtils.random(-1f, 1f) * currentPower
            camera.update()

            currentDuration += deltaTime
            return false
        }

        camera.position.set(origCameraPos)
        camera.update()
        return true
    }

    override fun reset() {
        maxDistortion = 0f
        duration = 0f
        storeCameraPos = true
        origCameraPos.set(Vector3.Zero)
        currentDuration = 0f
    }
}

private class CameraShakePool(private val gameCamera: Camera) : Pool<CameraShake>() {
    override fun newObject() = CameraShake().apply {
        this.camera = gameCamera
    }
}

class CameraShakeSystem(
    private val camera: Camera,
    private val gameEventManager: GameEventManager
) : EntitySystem(), GameEventListener {
    private val shakePool = CameraShakePool(camera)
    private val activeShakes = GdxArray<CameraShake>()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PlayerHit::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.PlayerHit::class, this)
    }

    override fun update(deltaTime: Float) {
        if(!activeShakes.isEmpty) {
            val shake = activeShakes.first()
            if(shake.update(deltaTime)) {
                activeShakes.removeIndex(0)
                shakePool.free(shake)
            }
        }
    }

    override fun onEvent(event: GameEvent) {
        if(activeShakes.size < 4) {
            activeShakes.add(shakePool.obtain().apply {
                duration = .25f
                maxDistortion = .25f
            })
        }
    }
}