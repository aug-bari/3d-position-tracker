package org.augbari.modules.positionTracker

fun main(args: Array<String>) {

    val broker = "tcp://broker.shiftr.io"
    val clientId = "PositionTrackerModule"
    val username = "pippobaudo"
    val password = "666tommaso"

    val tracker: Tracker? = Tracker(broker, clientId).connect(username, password)

    // Connection performed
    if(tracker != null) {

        // Tell arduino to start sending data
        tracker.sendMessage("accelerometer", "start")

        // Simple wait
        Thread.sleep(2000)

        // Stop receving data
        tracker.sendMessage("accelerometer", "stop")

        // Can disconnect from broker
        tracker.disconnect()

    }


}
