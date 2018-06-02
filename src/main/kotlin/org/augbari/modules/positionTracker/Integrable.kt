package org.augbari.modules.positionTracker

interface Integrable {

    var currentState: DoubleArray

    fun getValues(): DoubleArray

    fun setValues(array: DoubleArray)

}