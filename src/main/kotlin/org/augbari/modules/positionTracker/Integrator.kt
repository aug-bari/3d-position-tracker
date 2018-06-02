package org.augbari.modules.positionTracker

import kotlinx.coroutines.experimental.async

class Integrator {

    var integrablesObjects: MutableMap<Integrable, DoubleArray> = mutableMapOf()
    var outputObjectsMapping: MutableMap<Integrable, Integrable> = mutableMapOf()

    init {

        val nano2sec = 0.000000001
        var startTime: Double = System.nanoTime() * nano2sec
        var endTime: Double

        @Suppress("EXPERIMENTAL_FEATURE_WARNING")
        async {
            while (true) {

                endTime = System.nanoTime() * nano2sec
                for((integrableObject, integral) in integrablesObjects){

                    // Perform math
                    val deltaTime = endTime - startTime
                    val newIntegralValues: DoubleArray = integral.mapIndexed { index, d ->
                        d + (integrableObject.currentState[index] + integrableObject.getValues()[index]) * deltaTime / 2
                    }.toDoubleArray()

                    // Update object states
                    integrableObject.currentState = integrableObject.getValues()

                    // Set new integral values
                    integrablesObjects[integrableObject] = newIntegralValues

                    // If output object is set update it's values
                    outputObjectsMapping[integrableObject]?.setValues(newIntegralValues)

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

    fun setOutputObject(integrableObject: Integrable, outObject: Integrable) {
        outputObjectsMapping.put(integrableObject, outObject)
    }

}