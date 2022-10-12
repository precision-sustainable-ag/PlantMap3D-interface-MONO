package com.psa.OakdResearchInterface.data.main.depricated

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttClient




class MQTTHandler (private val context: Context, private val serverURI: String = "BROKER SERVER ADDRESS"){
    private val clientID get(): String = MqttClient.generateClientId() // generate a
    private val client get(): MqttAndroidClient = MqttAndroidClient(context, serverURI, clientID)
    private var clientIsConnected = false

    fun isClientConnected(): Boolean{
        return clientIsConnected

    }

    init {

    }

    fun connect(){
        client.connect().actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                clientIsConnected = true
                Log.i("PSA_RSI_DEBUGGING", "MQTT Connection Success")
            }
            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                clientIsConnected = false
                Log.e("PSA_RSI_DEBUGGING", "MQTT Connection Failure")
                exception.printStackTrace()
            }
        }
    }

}