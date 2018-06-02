package org.augbari.modules.positionTracker

import kotlinx.coroutines.experimental.async

class Integrator {

    var integrablesObjects: MutableMap<Integrable, DoubleArray> = mutableMapOf()

    init {

        var startTime: Double = System.nanoTime() / 1000000000.0
        var endTime: Double

        @Suppress("EXPERIMENTAL_FEATURE_WARNING")
        async {
            while (true) {

                endTime = System.nanoTime() / 1000000000.0
                for((integrableObject, integral) in integrablesObjects){

                    // Perform math
                    val deltaTime = endTime - startTime
                    // TODO : double integration
                    val newIntegralValues: DoubleArray = integrableObject.getValues().mapIndexed { index, d ->
                        d + integrableObject.currentState[index] * deltaTime / 2
                    }.toDoubleArray()

                    // Update object states
                    for ((index, value) in integrableObject.currentState.withIndex()) {
                        integrableObject.currentState[index] = integrableObject.getValues()[index]
                    }

                    // Set new integral values
                    integrablesObjects[integrableObject] = newIntegralValues

                }
                startTime = endTime

                Thread.sleep(1)
            }
        }

    }

    fun add(integrableObject: Integrable) {
        integrablesObjects.put(integrableObject, DoubleArray(integrableObject.currentState.size))
    }

    fun remove(integrableObject: Integrable) {
        integrablesObjects.remove(integrableObject)
    }

}