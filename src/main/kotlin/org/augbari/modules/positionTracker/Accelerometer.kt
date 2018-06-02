package org.augbari.modules.positionTracker

class Accelerometer(initialState: DoubleArray): Integrable {

    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double= 0.0

    override var currentState: DoubleArray = initialState

    fun getAccelerationVector(): DoubleArray {
        return doubleArrayOf(x, y, z)
    }

    override fun getValues(): DoubleArray {
        return getAccelerationVector()
    }

}