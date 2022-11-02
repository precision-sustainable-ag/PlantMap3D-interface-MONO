package com.psa.OakdResearchInterface.data



// Data Storage
const val DATA_DIRECTORY: String = "Estimation-Session-Data"
const val DATA_FILE_EXTENSION: String = ".bin"


// Logcat Tags
const val MAIN_TAG: String = "RSI_DEBUG"
const val TCP_TAG: String = MAIN_TAG + "_TCP"
const val DATA_TAG: String = MAIN_TAG + "_DAT"
const val UI_TAG: String = MAIN_TAG + "_UI"
const val UI_CLEAN_TAG: String = UI_TAG + "_CLEAN"


// Connection Info
const val TEST_IP: String = "192.168.0.103" // Manually enter the local IP here to connect to the test server
const val TEST_PORT: Int = 4242 // The port used to access the test server

const val PI_IP: String = "127.0.0.1" // Placeholder/Dummy IP for the raspberry pi, replace with real IP
const val PI_PORT: Int = 9002 // Placeholder/Dummy port for the raspberry pi, replace with real port

const val SERVER_IP: String = TEST_IP // The IP that will be connected to by this app
const val SERVER_PORT: Int = TEST_PORT // The port that will be connected to by this app


// Communication Key
const val CLOSE_MSG: String = "connection_closing"
const val HANDSHAKE_MSG: String = "connection_confirmation"


// State Key
const val BUTTON_PRESSED: String = "PRESSED"
const val BUTTON_NOT_PRESSED: String = "NOT_PRESSED"

