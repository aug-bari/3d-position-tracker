package org.augbari.modules.positionTracker

/**
 * Integrable object used it the tracker
 *
 * @param initialState specify initial state of this object
 * */
class Accelerometer(initialState: DoubleArray): Integrable {

    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double= 0.0

    override var currentState: DoubleArray = initialState

    /**
     * Set x, y, z values of this object
     *
     * @param array specific values to set
     * */
    override fun setValues(array: DoubleArray) {
        x = array[0]
        y = array[1]
        z = array[2]
    }

    /**
     * Get [x, y, z] values of this object
     * */
    override fun getValues(): DoubleArray {
        return doubleArrayOf(x, y, z)
    }

}