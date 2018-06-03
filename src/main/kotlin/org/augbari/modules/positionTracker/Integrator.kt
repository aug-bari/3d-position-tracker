package org.augbari.modules.positionTracker

class Integrator {

    var integrablesObjects: MutableMap<Integrable, DoubleArray> = mutableMapOf()
    var outputObjectsMapping: MutableMap<Integrable, Integrable> = mutableMapOf()

    val nano2sec = 0.000000001
    var startTime: Double = System.nanoTime() * nano2sec
    var endTime: Double = System.nanoTime() * nano2sec

    var last5velsIn = mutableListOf<Double>()

    fun integrate() {

        endTime = System.nanoTime() * nano2sec
        var deltaTime = endTime - startTime

        for((integrableObject, integral) in integrablesObjects){

            // Perform math
            val newIntegralValues: DoubleArray = integral.mapIndexed { index, d ->
                val increment = (integrableObject.currentState[index] + integrableObject.getValues()[index]) * deltaTime / 2

                if(index == 0 && integrableObject is Accelerometer) {
                    last5velsIn.add(d + increment)

                    if(last5velsIn.size > 2) {
                        last5velsIn.removeAt(0)

                        highPassFilter(last5velsIn.toDoubleArray(), deltaTime, .01).last()

                    } else {

                        0.0

                    }
                } else {
                    d + increment
                }
            }.toDoubleArray()

            // Update object states
            integrableObject.currentState = integrableObject.getValues()

            // Set new integral values
            integrablesObjects[integrableObject] = newIntegralValues

            // If output object is set update it's values
            outputObjectsMapping[integrableObject]?.setValues(newIntegralValues)

        }
        startTime = endTime
    }

    fun add(integrableObject: Integrable) {
        integrablesObjects[integrableObject] = DoubleArray(integrableObject.currentState.size)
    }

    fun remove(integrableObject: Integrable) {
        integrablesObjects.remove(integrableObject)
    }

    fun setOutputObject(integrableObject: Integrable, outObject: Integrable) {
        outputObjectsMapping[integrableObject] = outObject
    }

    fun highPassFilter(input: DoubleArray, dt: Double, RC: Double): DoubleArray {
        var output = DoubleArray(input.size)
        val alpha = RC / (RC + dt)
        for (i in input.indices) {
            if(i == 0) {
                output[0] = input[0]
            } else {
                output[i] = alpha * output[i - 1] + alpha * (input[i] - input[i - 1])
            }
        }
        return output
    }

}