package org.augbari.modules.positionTracker

import com.beust.klaxon.Klaxon
import org.eclipse.paho.client.mqttv3.*

class Tracker(private val broker: String, private val clientId: String): MqttCallback {

    lateinit var mqttClient: MqttClient
    lateinit var connOpts: MqttConnectOptions
    val accelerometer = Accelerometer(doubleArrayOf(0.0, 0.0, 0.0))
    val gyroscope = Gyroscope(doubleArrayOf(0.0, 0.0, 0.0))
    val integrator = Integrator()

    init {
        integrator.add(accelerometer)
        integrator.add(gyroscope)
    }

    fun connect(username: String, password: String): Boolean {
        try {

            // mqtt Client
            mqttClient = MqttClient(broker, clientId)

            // Connetion options
            mqttClient.setCallback(this)
            connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            connOpts.userName = username
            connOpts.password = password.toCharArray()

            // Attempt connecting
            System.out.println("Connecting to broker: $broker")
            mqttClient.connect(connOpts)
            println("Connected")

            return true

        } catch (e: MqttException) {
            println("ErrorCode: " + e.reasonCode)
            println("Message: " + e.message)
            println("Cause: " + e.cause)
            return false
        }
    }

    fun disconnect() {
        mqttClient.disconnect()
        println("Disconnected")
    }

    fun register(topic: String) {
        mqttClient.subscribe(topic)
    }

    fun sendMessage(topic: String, data: String) {
        mqttClient.publish(topic, MqttMessage(data.toByteArray()))
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val data = Klaxon().parseArray<Double>(message.toString())!!

        when(topic) {
            "acceleration " -> {
                accelerometer.x = data[0]
                accelerometer.y = data[1]
                accelerometer.z = data[2]
            }
            "gyro" -> {
                accelerometer.x = data[0]
                accelerometer.y = data[1]
                accelerometer.z = data[2]
            }
        }
    }

    override fun connectionLost(cause: Throwable?) {
        println("Connection to broker lost")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}
}