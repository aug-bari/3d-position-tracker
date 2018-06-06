package org.augbari.modules.positionTracker

/**
 * Integrable object interface
 * */
interface Integrable {

    var currentState: DoubleArray

    fun getValues(): DoubleArray

    fun setValues(array: DoubleArray)

}