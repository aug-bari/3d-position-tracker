package org.augbari.modules.positionTracker

class Accelerometer {

    var accX: Double = 0.0
    var accY: Double = 0.0
    var accZ: Double= 0.0

    fun getAccelerationVector(): DoubleArray {
        return doubleArrayOf(accX, accY, accZ)
    }

}