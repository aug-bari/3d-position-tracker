package org.augbari.modules.positionTracker.example

import org.augbari.modules.positionTracker.Tracker
import org.jfree.ui.RefineryUtilities
import java.util.*
import kotlin.streams.asSequence

class Example {

    companion object {

        val graph = Plotter("XY Series Demo")

        @JvmStatic
        fun main(args: Array<String>) {

            val broker = "tcp://broker.shiftr.io"
            val clientId = "PositionTrackerModule" + randomString(8)
            val username = "pippobaudo"
            val password = "666tommaso"

            val tracker = Tracker(broker, clientId)

            // TODO - Implement onDisconnect event handler

            // Try connecting
            if(tracker.connect(username, password)) {

                // Register to channel
                tracker.register("acceleration")
                tracker.register("gyro")

                // Tell arduino to start sending data
                tracker.sendMessage("accelerometer", "start")

                // Create custom callback for onMessageArrivedCallback - draw plot on Z axis
                var time = 0
                tracker.messageArrivedCallback = {
                    Example.graph.pos.add(time++, tracker.position.getValues()[2])
                    Example.graph.acc.add(time++, tracker.accelerometer.getValues()[2])
                    Example.graph.vel.add(time++, tracker.speed.getValues()[2])

                    if (time > 300) {
                        Example.graph.pos.remove(0)
                        Example.graph.acc.remove(0)
                        Example.graph.vel.remove(0)
                    }
                }

                // Plotter
                graph.pack()
                RefineryUtilities.centerFrameOnScreen(graph)
                graph.isVisible = true

                while(true) {
                    Thread.sleep(1000)
                }

                // Stop receving data
                //tracker.sendMessage("accelerometer", "stop")

                // Disconnect from broker
                //tracker.disconnect()
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

        fun round2Decimals(value: Double): Double {
            return Math.round(value * 100.0) / 100.0
        }

    }

}