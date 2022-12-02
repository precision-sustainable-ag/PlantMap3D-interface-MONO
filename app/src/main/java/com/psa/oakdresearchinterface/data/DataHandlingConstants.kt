package com.psa.oakdresearchinterface.data



// Data Storage
const val DATA_DIRECTORY: String = "Estimation-Session-Data"
const val DATA_FILE_EXTENSION: String = ".bin"
const val DATABASE_NAME: String = DATA_DIRECTORY


// Logcat Tags
const val MAIN_TAG: String = "RSI_DEBUG"
const val TCP_TAG: String = MAIN_TAG + "_TCP"
const val DATA_TAG: String = MAIN_TAG + "_DAT"
const val UI_TAG: String = MAIN_TAG + "_UI"
const val UI_CLEAN_TAG: String = UI_TAG + "_CLEAN"


// Connection Info
const val TEST_IP: String = "192.168.0.102" // Manually enter the local IP here to connect to the test server
const val TEST_PORT: Int = 4242 // The port used to access the test server

const val PI_IP: String = "127.0.0.1" // Placeholder/Dummy IP for the raspberry pi, replace with real IP
const val PI_PORT: Int = 4242 // Placeholder/Dummy port for the raspberry pi, replace with real port

const val SERVER_IP: String = TEST_IP // The IP that will be connected to by this app
const val SERVER_PORT: Int = TEST_PORT // The port that will be connected to by this app

const val MIN_IMGTYPE1_BYTES: Int = 7110
const val MIN_IMGTYPE2_BYTES: Int = 7986
const val READ_AFTER_SEND_DELAY: Long = 250 // msec
const val STREAM_READ_COMPLETE_CHECK_DELAY: Long = 50 //msec


// Outgoing Communication Key
const val HANDSHAKE_MSG: String = "connection_confirmation"
const val START_COLLECT_MSG: String = "start_collection"
const val PAUSE_COLLECT_MSG: String = "pause_collection"
const val UNPAUSE_COLLECT_MSG: String = "unpause_collection"
const val STOP_COLLECT_MSG: String = "stop_collection"
const val IMG_REQUEST_MSG: String = "requesting_img"
const val IMG_SECTION_REQUEST_MSG: String = "requesting_img_section"
const val IMG_VALID_MSG: String = "valid_img_received"


// Inbound Communication Key
const val STRING_INDICATOR_BYTE: Byte = 'S'.code.toByte()
const val IMG_INDICATOR_BYTE: Byte = 'I'.code.toByte()
const val IMG_SECTION_INDICATOR_BYTE: Byte = 'i'.code.toByte()
const val HANDSHAKE_CONFIRMATION: String = "handshake_complete"
const val START_CONFIRMATION: String = "collection_started"
const val PAUSE_CONFIRMATION: String = "collection_paused"
const val UNPAUSE_CONFIRMATION: String = "collection_unpaused"
const val STOP_CONFIRMATION: String = "collection_stopped"
const val IMG_TRANSFER_COMPLETE_MSG: String = "image_transfer_complete"




// State Key
const val BUTTON_PRESSED: String = "PRESSED"
const val BUTTON_NOT_PRESSED: String = "NOT_PRESSED"
const val COLLECT_NOT_STARTED: String = "NOT STARTED"
const val COLLECT_RUNNING: String = "RUNNING"
const val COLLECT_PAUSED: String = "PAUSED"

