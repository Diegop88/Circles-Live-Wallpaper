package dev.diegop88.circles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder

class RainbowWallpaper : AnimationWallpaper() {

    override fun onCreateEngine(): Engine = WallpaperEngine()

    inner class WallpaperEngine : AnimationEngine() {

        private var offsetX = 0
        private var offsetY = 0
        private var height = 0
        private var width = 0
        private var visibleWidth = 0
        private var iterationCount = 0
        private var circles = mutableSetOf<Circle>()
        private var paint = Paint()

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            this.height = height
            if (this.isPreview) {
                this.width = width
            } else {
                this.width = 2 * width
            }
            visibleWidth = width

            for (i in 0..10) {
                createRandomCircle()
            }

            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            offsetX = xPixelOffset
            offsetY = yPixelOffset
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
        }

        override fun onCommand(action: String?, x: Int, y: Int, z: Int, extras: Bundle?, resultRequested: Boolean): Bundle {
            if ("android.wallpaper.tap" == action) {
                createCircle((x - offsetX).toFloat(), (y - offsetY).toFloat())
            }
            return Bundle()
        }

        override fun iteration() {
            synchronized(circles) {
                val it = circles.iterator()
                while (it.hasNext()) {
                    val circle = it.next()
                    circle.tick()
                    if (circle.isDone) it.remove()
                }
                iterationCount++
                if (isPreview || iterationCount % 2 == 0) createRandomCircle()
            }
            super.iteration()
        }

        override fun drawFrame() {
            val holder = surfaceHolder
            var c: Canvas? = null
            try {
                c = holder.lockCanvas()
                c?.let { draw(it) }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c)
            }
        }

        private fun draw(c: Canvas) {
            c.save()
            c.drawColor(Color.BLACK)

            synchronized(circles) {
                for (circle in circles) {
                    if (circle.alpha == 0) continue

                    val minX = circle.x - circle.radius
                    if (minX > -offsetX + visibleWidth) continue

                    val maxX = circle.x + circle.radius
                    if (maxX < -offsetX) continue

                    paint.isAntiAlias = true

                    paint.color = Color.argb(circle.alpha, Color.red(circle.color), Color.green(circle.color), Color.blue(circle.color))
                    paint.style = Paint.Style.FILL

                    c.drawCircle(circle.x + offsetX, circle.y + offsetY, circle.radius, paint)

                    paint.color = Color.argb(circle.alpha, 63 + 3 * Color.red(circle.color) / 4, 63 + 3 * Color.green(circle.color) / 4, 63 + 3 * Color.blue(circle.color) / 4)
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 3.0f

                    c.drawCircle(circle.x + offsetX, circle.y + offsetY, circle.radius, paint)
                }
            }
            c.restore()
        }

        private fun createRandomCircle() {
            val x = (width * Math.random()).toFloat()
            val y = (height * Math.random()).toFloat()
            createCircle(x, y)
        }

        private fun createCircle(x: Float, y: Float) {
            val radius = (40 + 20 * Math.random()).toFloat()
            var yFraction = y / height.toFloat()
            yFraction = yFraction + 0.05f - (0.1f * Math.random()).toFloat()
            if (yFraction < 0.0f) yFraction += 1.0f
            if (yFraction > 1.0f) yFraction -= 1.0f
            val color = getColor(yFraction)
            val steps = 40 + (20 * Math.random())
            val circle = Circle(x, y, radius, color, steps)
            synchronized(circles) { circles.add(circle) }
        }

        private fun getColor(yFraction: Float) = Color.HSVToColor(floatArrayOf(360.0f * yFraction, 1.0f, 1.0f))
    }
}
