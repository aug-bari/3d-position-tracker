package org.augbari.modules.positionTracker

class Gyroscope(initialState: DoubleArray): Integrable {

    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double= 0.0

    override var currentState: DoubleArray = initialState

    fun getGyroscopeVector(): DoubleArray {
        return doubleArrayOf(x, y, z)
    }

    override fun getValues(): DoubleArray {
        return getGyroscopeVector()
    }

}