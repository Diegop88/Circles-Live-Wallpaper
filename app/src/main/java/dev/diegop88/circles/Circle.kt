package dev.diegop88.circles

class Circle(var x: Float, var y: Float, var radius: Float, val color: Int, private var steps: Double) {

    private var origRadius: Float = radius
    private var deltaRadius: Float = 0.5f * radius
    private var origX: Float = x
    private var origY: Float = y
    private var deltaY: Float = (40.0 * Math.random() - 20.0).toFloat()
    private var deltaX: Float = (40.0 * Math.random() - 20.0).toFloat()
    private var currentStep = 0

    var alpha: Int = 0

    fun tick() {
        currentStep++
        val fraction = (currentStep / steps).toFloat()
        radius = origRadius + fraction * deltaRadius
        x = origX + fraction * deltaX
        y = origY + fraction * deltaY
        alpha = if (fraction <= 0.25f) {
            (128 * 4.0f * fraction).toInt()
        } else {
            (-128 * (fraction - 1) / 0.75f).toInt()
        }
    }

    val isDone: Boolean
        get() = currentStep > steps
}
