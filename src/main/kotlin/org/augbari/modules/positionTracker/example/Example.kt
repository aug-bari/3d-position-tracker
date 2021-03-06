package org.augbari.modules.positionTracker.example

import org.augbari.modules.positionTracker.Tracker
import org.jfree.ui.RefineryUtilities
import java.util.*
import kotlin.streams.asSequence

/**
 * Example class used to show how this library works.
 * */
class Example {

    companion object {

        val graph = Plotter("Graph")

        /**
         * Main static method of Example class.
         * @param args arguments to supply to main method - not used here.
         * */
        @JvmStatic
        fun main(args: Array<String>) {

            // Configuration vars
            val broker = "tcp://broker.shiftr.io"
            val clientId = "PositionTrackerModule"// + randomString(8) // to change client id name
            val username = "pippobaudo"
            val password = "666tommaso"

            // Create new tracker object
            val tracker = Tracker(broker, clientId)

            // Try connecting
            if(tracker.connect(username, password)) {

                // Register to channel
                tracker.register("mpu6050")

                // Create custom callback for onMessageArrivedCallback - plot on graph
                var time = 0.0
                tracker.onMessageArrivedCallback = {
                    Example.graph.posX.add(time, tracker.position.getValues()[0])
                    Example.graph.posY.add(time, tracker.position.getValues()[1])
                    Example.graph.posZ.add(time, tracker.position.getValues()[2])

                    if (time > 5) {
                        Example.graph.posX.remove(0)
                        Example.graph.posY.remove(0)
                        Example.graph.posZ.remove(0)
                    }

                    time += 1 / tracker.frequency
                }

                // Configure plotter
                graph.pack()
                RefineryUtilities.centerFrameOnScreen(graph)
                graph.isVisible = true

                // Keep this thread alive
                while(tracker.mqttClient.isConnected) {

                    // To get time between packets
                    println(1 / tracker.frequency)

                    Thread.sleep(1000)
                }

            }

        }

        /**
         * Private method used to generate random strings.
         * @param length length of output string.
         * */
        private fun randomString(length: Long): String {
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            return Random()
                    .ints(length, 0, source.length)
                    .asSequence()
                    .map(source::get)
                    .joinToString("")
        }

    }

}