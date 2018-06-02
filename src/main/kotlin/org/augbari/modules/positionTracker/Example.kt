package org.augbari.modules.positionTracker

import java.util.*
import kotlin.streams.asSequence

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

        for (i in 1..1000) {

            println(tracker.integrator.integrablesObjects.getValue(tracker.accelerometer).map { it.toString() })

            Thread.sleep(100)
        }

        // Stop receving data
        tracker.sendMessage("accelerometer", "stop")

        // Disconnect from broker
        tracker.disconnect()
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