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
package com.invozone.contactlesspayment.chat

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.invozone.contactlesspayment.bluetooth.Message
import com.invozone.contactlesspayment.R
import com.invozone.contactlesspayment.bluetooth.ChatServer
import com.invozone.contactlesspayment.databinding.FragmentBluetoothChatBinding
import com.invozone.contactlesspayment.gone
import com.invozone.contactlesspayment.visible
import kotlinx.android.synthetic.main.receive_payment.*
import kotlinx.android.synthetic.main.send_payment.view.*

private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {

    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
//                chatWith(device)
            }
            is DeviceConnectionState.Disconnected -> {
//                showDisconnected()
                findNavController().popBackStack()
            }
        }
    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: have device $device")


        ChatServer.setCurrentChatConnection(device)
    }

    private val messageObserver = Observer<Message> { message ->

        when(message) {
            is Message.RemoteMessage -> {
                Log.d("Remote message","${message.status} ${message.amount}")

                if(message.status=="SendMoney")
                {
                    amountReceived.text= message.amount.toString()
                    ChatServer.sendMessage("TransactionSuccessful", message.amount ?: 0)

                }
                if(message.status=="TransactionSuccessful")
                {
                   findNavController().navigate(R.id.action_bluetoothChatFragment_to_transactionSuccessFragment)
                }
            }
            is Message.LocalMessage -> {
                Log.d("Local message","${message.status} ${message.amount}")

                if(message.status=="TransactionSuccessful")
                {
                    findNavController().navigate(R.id.action_bluetoothChatFragment_to_transactionSuccessFragment)
                }
            }
        }

    }

    private val adapter = MessageAdapter()

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)

        Log.d(TAG, "chatWith: set adapter $adapter")

        Log.d("isServer",ChatServer.isServer.toString())
        if(ChatServer.isServer)
        {
            Log.d("tag","Server")
            binding.sendPaymentContainer.visible()
            binding.receivePaymentContainer.gone()
        }
        else{
            Log.d("tag","Client")
            binding.receivePaymentContainer.visible()
            binding.sendPaymentContainer.gone()

        }

        binding.sendPaymentContainer.sendAmount.setOnClickListener{
            val amount = binding.sendPaymentContainer.amountField?.text.toString().trim()

            if(amount.isNotEmpty())
            {
                ChatServer.sendMessage("SendMoney", amount = amount.toInt())
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.chat_title)
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        ChatServer.disconnectConnection()
        _binding = null
    }

//    private fun chatWith(device: BluetoothDevice) {
//        binding.connectedContainer.visible()
//        binding.notConnectedContainer.gone()
//
//        val chattingWithString = resources.getString(R.string.chatting_with_device, device.address)
//        binding.connectedDeviceName.text = chattingWithString
//        binding.sendMessage.setOnClickListener {
//            val message = binding.messageText.text.toString()
//            // only send message if it is not empty
//            if (message.isNotEmpty()) {
//                ChatServer.sendMessage(message,2000)
//
//                // clear message
//                binding.messageText.setText("")
//            }
//        }
//    }

    private fun showDisconnected() {
        hideKeyboard()
//        binding.notConnectedContainer.visible()
//        binding.connectedContainer.gone()
        findNavController().popBackStack()

    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}