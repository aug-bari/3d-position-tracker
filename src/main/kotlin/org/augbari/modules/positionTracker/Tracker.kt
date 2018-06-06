package org.augbari.modules.positionTracker

import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject

/**
 * Tracker object which manages acceleration data received from mqtt broker.
 *
 * Thanks to Integrator class and Integrable objects like Accelerometer, Speed and Position it can determine the
 * position in a 3d space environment.
 *
 * @param broker Broker URI in a standard form (such as "tcp://mywebsite.com").
 * @param clientId Name of the client to be identified in the network.
 * */
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

    /**
     * Connect to mqtt server providing username and password.
     *
     * @param username Authentication username used to connect to mqtt server.
     * @param password Authentication password used to connect to mqtt server.
     * */
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

    /**
     * Disconnect from mqtt server.
     * */
    fun disconnect() {
        mqttClient.disconnect()
        println("Disconnected")
    }

    /**
     * Subscribe to mqtt topic.
     *
     * @param topic Topic to register to.
     * */
    fun register(topic: String) {
        mqttClient.subscribe(topic, qos)
    }

    /**
     * Send message to a specific topic with a specific content.
     *
     * @param topic Topic to which send the message.
     * @param data String data to send to topic.
     * */
    fun sendMessage(topic: String, data: String) {
        mqttClient.publish(topic, data.toByteArray(), qos, true)
    }

    /**
     * On message arrived callback.
     *
     * Required message format := {"gyro":{"x":0.26267204,"y":0.06963864,"z":0.26145032},"accel":{"x":-0.1656,"y":0.8746,"z":-0.41939998}}.
     *
     * @param topic Topic from which the message is arrived.
     * @param message MqttMessage arrived from server.
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

    /**
     * On connection lost from mqtt server callback.
     *
     * @param cause - check the Eclipse PAHO documentation.
     * */
    override fun connectionLost(cause: Throwable?) {
        println("Connection to broker lost")
    }

    /**
     * On message delivery complete callback.
     *
     * @param token - check the Eclipse PAHO documentation.
     * */
    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    /**
     * Private method to calculate frequency of incoming packets.
     * */
    private fun calcFrequency(): Double {
        val deltaTime = System.nanoTime() * nano2sec - previousPacketTime
        previousPacketTime = System.nanoTime() * nano2sec
        return 1 / deltaTime
    }

}
