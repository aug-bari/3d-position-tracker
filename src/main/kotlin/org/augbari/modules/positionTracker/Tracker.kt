package org.augbari.modules.positionTracker

import org.eclipse.paho.client.mqttv3.*

class Tracker(private val broker: String, private val clientId: String): MqttCallback {

    lateinit var mqttClient: MqttClient
    lateinit var connOpts: MqttConnectOptions
    val accelerometer = Accelerometer()

    fun connect(username: String, password: String): Tracker? {
        try {

            // mqtt Client
            mqttClient = MqttClient(broker, clientId)

            // Connetion options
            connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            connOpts.userName = username
            connOpts.password = password.toCharArray()

            // Attempt connecting
            System.out.println("Connecting to broker: $broker")
            mqttClient.connect(connOpts)
            println("Connected")

            return this

        } catch (e: MqttException) {
            println("ErrorCode: " + e.reasonCode)
            println("Message: " + e.message)
            println("Cause: " + e.cause)
            return null
        }
    }

    fun disconnect() {
        mqttClient.disconnect()
        println("Disconnected")
    }

    fun sendMessage(topic: String, data: String) {
        mqttClient.publish(topic, MqttMessage(data.toByteArray()))
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val acceleration = message.toString().split(delimiters = *arrayOf(",")).map { it.toDouble() }
        accelerometer.accX = acceleration[0]
        accelerometer.accY = acceleration[1]
        accelerometer.accZ = acceleration[2]
    }

    override fun connectionLost(cause: Throwable?) {
        println("Connection to broker lost")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}
}