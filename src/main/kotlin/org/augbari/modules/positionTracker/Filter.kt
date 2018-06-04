package org.augbari.modules.positionTracker

class Filter(private val filterType: FilterType, val RC: Double) {

    private var previousValues: DoubleArray = doubleArrayOf()

    fun filter(inputArray: DoubleArray, dt: Double): DoubleArray {

        // Initialize previousValues
        if(previousValues.isEmpty()) {
            previousValues = DoubleArray(inputArray.size)
        }

        return inputArray.mapIndexed { index, double -> filter(double, index, dt) }.toDoubleArray()
    }

    private fun filter(value: Double, previousValueIndex: Int, dt: Double): Double {
        val result = when(filterType) {
            FilterType.highPassFilter -> highPassFilter(doubleArrayOf(previousValues[previousValueIndex], value), dt)
            FilterType.lowPassFilter -> lowPassFilter(doubleArrayOf(previousValues[previousValueIndex], value), dt)
        }

        // Save previous value
        previousValues[previousValueIndex] = value

        return result
    }

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