package org.augbari.modules.positionTracker

class Integrator {

    // Define integrable objects
    private var integrablesObjects: MutableMap<Integrable, DoubleArray> = mutableMapOf()
    private var outputObjectsMapping: MutableMap<Integrable, Integrable> = mutableMapOf()

    // Time vars
    private val nano2sec = 0.000000001
    private var startTime: Double = System.nanoTime() * nano2sec
    private var endTime: Double = System.nanoTime() * nano2sec

    // Filtering
    private var inputFilterMapping: MutableMap<Integrable, Filter> = mutableMapOf()
    private var outputFilterMapping: MutableMap<Integrable, Filter> = mutableMapOf()

    fun integrate() {

        endTime = System.nanoTime() * nano2sec
        val deltaTime = endTime - startTime

        for((integrableObject, integral) in integrablesObjects) {

            // Apply filters to input
            inputFilterMapping.forEach { integrable, filter ->
                if(integrable == integrableObject) {
                    integrableObject.setValues(inputFilterMapping[integrable]!!.filter(integrableObject.getValues(), deltaTime))
                }
            }

            // Perform integration
            var newIntegralValues: DoubleArray = integral.mapIndexed { index, d ->
                d + (integrableObject.currentState[index] + integrableObject.getValues()[index]) * deltaTime / 2
            }.toDoubleArray()

            // Apply filters to output (to integrated values)
            outputObjectsMapping.forEach { integrableIn, integrableOut ->
                if(outputFilterMapping[integrableIn] != null && integrableOut == integrableObject) {
                    newIntegralValues = outputFilterMapping[integrableIn]!!.filter(newIntegralValues, deltaTime)
                }
            }

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

    fun setInputFilter(integrableObject: Integrable, filterType: FilterType, RC: Double) {
        inputFilterMapping[integrableObject] = Filter(filterType, RC)
    }

    fun setOutputFilter(integrableObject: Integrable, filterType: FilterType, RC: Double) {
        outputFilterMapping[integrableObject] = Filter(filterType, RC)
    }

}