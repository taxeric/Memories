package com.lanier.memories.widget

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import com.lanier.memories.entity.Particle
import kotlin.math.cos
import kotlin.math.sin

/**
 * Author: Turtledove
 * Date  : on 2023/5/16
 * Desc  :
 */
object ParticleHelper {

    private val particles = mutableListOf<Particle>()
    private val parentLocation = IntArray(2)
    var startX = 540f
    var startY = 540f

    fun initRes(resources: Resources, ids: IntArray, topView: View) {
        ids.forEach { drawableId ->
            particles.add(
                Particle.obtainParticle(
                    BitmapFactory.decodeResource(resources, drawableId)
                )
            )
        }
        topView.getLocationInWindow(parentLocation)
    }

    fun start(emiter: View, topView: View) {
        val location = IntArray(2)
        emiter.getLocationInWindow(location)
        startX = (location[0] + emiter.width / 2 - parentLocation[0]).toFloat()
        startY = (location[1] - parentLocation[1]).toFloat()
        println(">>>> $startX")
        println(">>>> $startY")
        val particleView = ParticleView(topView.context)
        (topView as ViewGroup).addView(particleView)
        particleView.resetParticles(particles)
        startAnim(particleView, topView)
    }

    private fun startAnim(particleView: ParticleView, topView: ViewGroup) {
        val animator = ObjectAnimator.ofInt(0, 1)
            .setDuration(1000L)
        animator.addUpdateListener { listener ->
            particleView.mm(listener)
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                resetParticles()
            }

            override fun onAnimationEnd(animation: Animator) {
                topView.removeView(particleView)
                topView.postInvalidate()
            }
        })
        animator.start()
    }

    private fun resetParticles() {
        particles.forEachIndexed { index, it ->
            val m = Particle.resetXYV(it)
            particles[index] = m
        }
    }
}