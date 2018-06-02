package org.augbari.modules.positionTracker

class Gyroscope(initialState: DoubleArray): Integrable {

    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double= 0.0

    override var currentState: DoubleArray = initialState

    override fun setValues(array: DoubleArray) {
        x = array[0]
        y = array[1]
        z = array[2]
    }

    override fun getValues(): DoubleArray {
        return doubleArrayOf(x, y, z)
    }

}