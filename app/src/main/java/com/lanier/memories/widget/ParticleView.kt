package com.lanier.memories.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.lanier.memories.entity.Particle
import kotlin.math.cos
import kotlin.math.sin

/**
 * Author: Turtledove
 * Date  : on 2023/5/16
 * Desc  :
 */
class ParticleView(
    context: Context,
    attrs: AttributeSet?,
    def: Int,
): View(context, attrs, def) {

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, -1)
    constructor(context: Context): this(context, null, -1)

    private val paint = Paint()
    private val particles = mutableListOf<Particle>()

    fun resetParticles(list: List<Particle>) {
        particles.clear()
        particles.addAll(list)
    }

    fun mm(listener: ValueAnimator) {
        particles.forEachIndexed { index, it ->
            var time = listener.animatedFraction
            time *= 10
            val ma = 1 - listener.animatedFraction
            val mx = ParticleHelper.startX - it.startXV * time * cos(it.angle)
            val my = ParticleHelper.startY - (it.startYV * time * sin(it.angle) - 9.8 * time * time / 2).toFloat()
            val m = it.copy(
                alpha = ma,
                x = mx,
                y = my
            )
            particles[index] = m
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        println(">>>> 重绘")
        particles.forEach {
            paint.alpha = (it.alpha * 255).toInt()
            println(">>>> update  ${it.x} ${it.y}")
            canvas.drawBitmap(it.bitmap, it.x - it.width / 2, it.y - it.height / 2, paint)
        }
    }
}