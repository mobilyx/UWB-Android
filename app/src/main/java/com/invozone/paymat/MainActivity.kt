/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.invozone.paymat

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.invozone.paymat.bluetooth.ChatServer
import com.invozone.paymat.chat.DeviceConnectionState

private const val TAG = "BluetoothLeChat"

class MainActivity : AppCompatActivity() {


    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d("Connection Observer", "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d("Connection", "Gatt connection observer: have device $device")
//                chatWith(device)
            }
            is DeviceConnectionState.Disconnected -> {
//                showDisconnected()
            }
        }

    }
    
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("BluetoothLeChat", "Requesting needed permissions")
        requestPermissions.launch(arrayOf(
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
            ))
    }

    // Run the chat server as long as the app is on screen
    override fun onStart() {
        super.onStart()
        ChatServer.startServer(application)
        ChatServer.connectionRequest.observe(this, connectionRequestObserver)
        ChatServer.deviceConnection.observe(this, deviceConnectionObserver)
    }

    override fun onStop() {
        super.onStop()
        ChatServer.stopServer()
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d(TAG, "${it.key} = ${it.value}")
            }
        }
}