package org.augbari.modules.positionTracker

/**
 * Filter class to use to filter signals.
 *
 * @param filterType filter type to use.
 * @param RC time constant to define.
 * */
class Filter(private val filterType: FilterType, val RC: Double) {

    // Signal as mutable list of double array (list of [x, y, z])
    private var signal: MutableList<DoubleArray> = mutableListOf()

    /**
     * Filter signal using the filter type and RC specified.
     *
     * @param inputArray input signal as double array.
     * @param dt delta time between this step and previous one.
     * */
    fun filter(inputArray: DoubleArray, dt: Double): DoubleArray {

        if(signal.isEmpty()) {
            // Initialize signal
            signal = mutableListOf(DoubleArray(inputArray.size))
        } else {
            // Or add this to previous values
            signal.add(inputArray)

            // Clear old points
            if(signal.size > 1000) {
                signal.removeAt(0)
            }
        }

        return inputArray.mapIndexed { index, double -> filter(index, dt) }.toDoubleArray()
    }

    /**
     * Private method used to filter signal on one dimension
     * */
    private fun filter(previousValueIndex: Int, dt: Double): Double {
        return when(filterType) {
            FilterType.highPassFilter -> highPassFilter(signal.map { it[previousValueIndex] }.toDoubleArray(), dt)
            FilterType.lowPassFilter -> lowPassFilter(signal.map { it[previousValueIndex] }.toDoubleArray(), dt)
        }
    }

    /**
     * Private method used to apply high pass filter to signal
     * */
    private fun highPassFilter(input: DoubleArray, dt: Double): Double {
        val output = DoubleArray(input.size)
        val alpha = RC / (RC + dt)
        input.forEachIndexed { index, d ->
            if(index == 0) {
                output[0] = input[0]
            } else {
                output[index] = alpha * output[index - 1] + alpha * (input[index] - input[index - 1])
            }
        }
        return output.last()
    }

    /**
     * Private method used to apply high low filter to signal
     * */
    private fun lowPassFilter(input: DoubleArray, dt: Double): Double {
        val output = DoubleArray(input.size)
        val alpha = RC / (RC + dt)
        input.forEachIndexed { index, d ->
            if(index == 0) {
                output[0] = alpha * input[0]
            } else {
                output[index] = alpha * output[index] + (1 - alpha) * output[index - 1]
            }
        }
        return output.last()
    }

}