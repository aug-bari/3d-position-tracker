package org.augbari.modules.positionTracker

class Filter(private val filterType: FilterType, val RC: Double) {

    private var previousValues: MutableList<DoubleArray> = mutableListOf()

    fun filter(inputArray: DoubleArray, dt: Double): DoubleArray {

        if(previousValues.isEmpty()) {
            // Initialize previousValues
            previousValues = mutableListOf(DoubleArray(inputArray.size))
        } else {
            // Or add this to previous values
            previousValues.add(inputArray)

            // Clear old points
            if(previousValues.size > 1000) {
                previousValues.removeAt(0)
            }
        }

        return inputArray.mapIndexed { index, double -> filter(index, dt) }.toDoubleArray()
    }

    private fun filter(previousValueIndex: Int, dt: Double): Double {
        return when(filterType) {
            FilterType.highPassFilter -> highPassFilter(previousValues.map { it[previousValueIndex] }.toDoubleArray(), dt)
            FilterType.lowPassFilter -> lowPassFilter(previousValues.map { it[previousValueIndex] }.toDoubleArray(), dt)
        }
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