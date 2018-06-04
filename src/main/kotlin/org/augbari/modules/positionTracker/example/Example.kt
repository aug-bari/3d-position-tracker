package org.augbari.modules.positionTracker.example

import org.augbari.modules.positionTracker.Tracker
import org.jfree.ui.RefineryUtilities
import java.util.*
import kotlin.streams.asSequence

class Example {

    companion object {

        val graph = Plotter("Graph")

        @JvmStatic
        fun main(args: Array<String>) {

            val broker = "tcp://broker.shiftr.io"
            val clientId = "PositionTrackerModule"// + randomString(8)
            val username = "pippobaudo"
            val password = "666tommaso"

            val tracker = Tracker(broker, clientId)

            // Try connecting
            if(tracker.connect(username, password)) {

                // Register to channel
                tracker.register("mpu6050")

                // Create custom callback for onMessageArrivedCallback
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

                // Plotter
                graph.pack()
                RefineryUtilities.centerFrameOnScreen(graph)
                graph.isVisible = true

                // Keep this thread live
                while(tracker.mqttClient.isConnected) {

                    // To get time between packets
                    println(1 / tracker.frequency)

                    Thread.sleep(1000)
                }

            }

        }

        fun randomString(length: Long): String {
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            return Random()
                    .ints(length, 0, source.length)
                    .asSequence()
                    .map(source::get)
                    .joinToString("")
        }

    }

}