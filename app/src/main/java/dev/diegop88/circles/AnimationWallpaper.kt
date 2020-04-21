package dev.diegop88.circles

import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

abstract class AnimationWallpaper : WallpaperService() {

    abstract inner class AnimationEngine : Engine() {

        private val mHandler = Handler()
        private var mVisible = false
        private val mIteration = Runnable {
            iteration()
            drawFrame()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            iteration()
            drawFrame()
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            iteration()
            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            mVisible = visible
            if (visible) {
                iteration()
                drawFrame()
            } else {
                mHandler.removeCallbacks(mIteration)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            mVisible = false
            mHandler.removeCallbacks(mIteration)
        }

        override fun onDestroy() {
            super.onDestroy()
            mHandler.removeCallbacks(mIteration)
        }

        open fun iteration() {
            mHandler.removeCallbacks(mIteration)
            if (mVisible) {
                mHandler.postDelayed(mIteration, 40)
            }
        }

        abstract fun drawFrame()
    }
}
