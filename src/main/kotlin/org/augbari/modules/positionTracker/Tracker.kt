package org.augbari.modules.positionTracker

import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject

class Tracker(private val broker: String, private val clientId: String): MqttCallback {

    // MQTT vars
    lateinit var mqttClient: MqttClient
    lateinit var connOpts: MqttConnectOptions
    private val qos = 2

    // Time vars
    var frequency = Double.POSITIVE_INFINITY
    var previousPacketTime = 0.0
    private val nano2sec = 0.000000001

    // Objects
    val accelerometer = Accelerometer(doubleArrayOf(0.0, 0.0, 0.0))
    val gyroscope = Gyroscope(doubleArrayOf(0.0, 0.0, 0.0))
    val speed = Speed(doubleArrayOf(0.0, 0.0, 0.0))
    val position = Position(doubleArrayOf(0.0, 0.0, 0.0))
    private val integrator = Integrator()

    // Custom callback
    var onMessageArrivedCallback: () -> Unit = {}

    init {
        // Add objects to integrator
        integrator.add(accelerometer)
        integrator.add(gyroscope)
        integrator.add(speed)

        // Set integration output
        integrator.setOutputObject(accelerometer, speed)
        integrator.setOutputObject(speed, position)

        // Set output filter for speed
        integrator.setInputFilter(accelerometer, FilterType.highPassFilter, .1)
        //integrator.setInputFilter(accelerometer, FilterType.lowPassFilter, 0.001)
        //integrator.setInputFilter(speed, FilterType.highPassFilter, 1.0)
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
        mqttClient.subscribe(topic, qos)
    }

    fun sendMessage(topic: String, data: String) {
        mqttClient.publish(topic, data.toByteArray(), qos, true)
    }

    /**
     * Required message format := {"gyro":{"x":0.26267204,"y":0.06963864,"z":0.26145032},"accel":{"x":-0.1656,"y":0.8746,"z":-0.41939998}}
     * */
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if(previousPacketTime == 0.0) {
            // Set start time if it isn't initialized
            previousPacketTime = System.nanoTime() * nano2sec
        } else {
            // Else calculate frequency of incoming packets
            frequency = calcFrequency()
        }

        // Parse JSON data
        val data = JSONObject(message.toString())
        val gyro = data.getJSONObject("gyro")
        val accel = data.getJSONObject("accel")

        // Check topic
        when(topic) {
            "mpu6050" -> {
                accelerometer.setValues(doubleArrayOf(accel.getDouble("x"), accel.getDouble("y"), accel.getDouble("z")))
                gyroscope.setValues(doubleArrayOf(gyro.getDouble("x"), gyro.getDouble("y"), gyro.getDouble("z")))
            }
            "disconnect" -> {
                disconnect()
            }
        }

        // Integrator step
        integrator.integrate()

        // Custom callback
        onMessageArrivedCallback()
    }

    override fun connectionLost(cause: Throwable?) {
        println("Connection to broker lost")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    fun calcFrequency(): Double {
        val deltaTime = System.nanoTime() * nano2sec - previousPacketTime
        previousPacketTime = System.nanoTime() * nano2sec
        return 1 / deltaTime
    }

}
