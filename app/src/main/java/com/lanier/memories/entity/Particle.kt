package com.lanier.memories.entity

import android.graphics.Bitmap
import android.graphics.Matrix
import kotlin.random.Random

/**
 * Author: Turtledove
 * Date  : on 2023/5/16
 * Desc  :
 */
data class Particle(
    val x: Float,
    val y: Float,
    val startXV: Float,
    val startYV: Float,
    val angle: Float,
    val alpha: Float,
    val bitmap: Bitmap,
    val width: Int,
    val height: Int
) {

    companion object {

        fun obtainParticle(bitmap: Bitmap): Particle {
            val scale = Math.random() * 0.3f + 0.7f
            val w = (bitmap.width * scale).toInt()
            val h = (bitmap.height * scale).toInt()
            val r = (Math.random() * 180 - 90).toFloat()
            val matrix = Matrix()
            matrix.setRotate(r)
            val b = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false)
            bitmap.recycle()
            return Particle(
                x = 0f,
                y = 0f,
                startXV = Random.nextInt(150) * if (Random.nextBoolean()) 1f else -1f,
                startYV = Random.nextInt(170) * if (Random.nextBoolean()) 1f else -1f,
                angle = (Random.nextInt(360) * Math.PI / 180).toFloat(),
                alpha = 1f,
                bitmap = b,
                width = w,
                height = h,
            )
        }

        fun resetXYV(particle: Particle): Particle {
            val r = (Math.random() * 180 - 90).toFloat()
            val matrix = Matrix()
            matrix.setRotate(r)
            return Particle(
                x = 0f,
                y = 0f,
                startXV = Random.nextInt(150) * if (Random.nextBoolean()) 1f else -1f,
                startYV = Random.nextInt(170) * if (Random.nextBoolean()) 1f else -1f,
                angle = (Random.nextInt(360) * Math.PI / 180).toFloat(),
                alpha = 1f,
                bitmap = particle.bitmap,
                width = particle.width,
                height = particle.height,
            )
        }
    }
}
