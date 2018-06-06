package org.augbari.modules.positionTracker

/**
 * Integrator class to perform integration of integrable objects.
 * */
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

    /**
     * Method called each time we need to do an integration step.
     *
     * The solution provided here is the trapezoidal rule.
     *
     * This includes low pass and high pass filters to filter signal coming from integroble objects.
     * */
    fun integrate() {

        endTime = System.nanoTime() * nano2sec
        val deltaTime = endTime - startTime

        // Integrate for each integrable object
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
            outputFilterMapping.forEach { integrable, filter ->
                if(integrable == integrableObject) {
                    newIntegralValues = outputFilterMapping[integrable]!!.filter(newIntegralValues, deltaTime)
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

    /**
     * Add integrable object to integrable objects container.
     *
     * @param integrableObject integrable object to add to container.
     * */
    fun add(integrableObject: Integrable) {
        integrablesObjects[integrableObject] = DoubleArray(integrableObject.currentState.size)
    }

    /**
     * Remove integrable object from integrable objects container.
     *
     * @param integrableObject integrable object to remove from container.
     * */
    fun remove(integrableObject: Integrable) {
        integrablesObjects.remove(integrableObject)
    }

    /**
     * Set an integrable object as output of integration of another integrable object.
     *
     * @param integrableObject first integrable object to integrate.
     * @param outObject output object in which store integrated value.
     * */
    fun setOutputObject(integrableObject: Integrable, outObject: Integrable) {
        outputObjectsMapping[integrableObject] = outObject
    }

    /**
     * Add a new input filter to an integrable object.
     *
     * The input signal is filtered before it is integrated.
     *
     * @param integrableObject first integrable object to integrate.
     * @param filterType type of filter to use.
     * @param RC time constant to define.
     * */
    fun setInputFilter(integrableObject: Integrable, filterType: FilterType, RC: Double) {
        inputFilterMapping[integrableObject] = Filter(filterType, RC)
    }

    /**
     * Add a new output filter to an integrable object.
     *
     * The output signal is filtered after the integration
     *
     * @param integrableObject first integrable object to integrate
     * @param filterType type of filter to use
     * @param RC time constant to define
     * */
    fun setOutputFilter(integrableObject: Integrable, filterType: FilterType, RC: Double) {
        outputFilterMapping[integrableObject] = Filter(filterType, RC)
    }

}