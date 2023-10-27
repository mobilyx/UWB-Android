package com.invozone.contactlesspayment

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.invozone.contactlesspayment.Adapter.TransactionAdapter
import com.invozone.contactlesspayment.bluetooth.REQUEST_ENABLE_BT
import com.invozone.contactlesspayment.databinding.FragmentSelectionBinding
import com.invozone.contactlesspayment.model.TransactionDataModel
import com.invozone.contactlesspayment.model.TransactionStatus
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.invozone.contactlesspayment.scan.DeviceScanViewModel

private const val LOCATION_REQUEST_CODE = 0

class SelectionFragment : Fragment(R.layout.fragment_selection) {

    private lateinit var viewModel: DeviceScanViewModel
    private lateinit var binding: FragmentSelectionBinding



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = FragmentSelectionBinding.bind(view)


        val recyclerview = binding.transactionRecyclerView

        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        val data = ArrayList<TransactionDataModel>()

        data.add(TransactionDataModel(name = "Jennifer Stone",TransactionStatus.Received, amount = 2000))
        data.add(TransactionDataModel(name = "James Rock",TransactionStatus.Pending, amount = 2400))
        data.add(TransactionDataModel(name = "Alexander Thomas",TransactionStatus.Failed, amount = 3000))

        val adapter = TransactionAdapter(data)

        recyclerview.adapter = adapter

        viewModel = ViewModelProvider(this).get(DeviceScanViewModel::class.java)
        viewModel.deviceConnectionRequest.observe(viewLifecycleOwner, Observer { newData ->
            Log.d("ConnectionRequest","request");

            // Handle the updated data here
            // This code will be executed when the data changes
        })






        binding.rlSendPayment.setOnClickListener {
//            startPayment()
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    private fun startPayment() {
//        findNavController().navigate(R.id.action_bluetooth_fragment)
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.chat_title)
        checkLocationPermission()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result:Boolean=checkLocationPermission()
                    if(result) {
                        findNavController().navigate(R.id.action_selectionFragment_to_deviceListFragment)
//                        ChatServer.stopServer()
                    }

                }
                super.onActivityResult(requestCode, resultCode, data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Navigate to the chat fragment
//                    findNavController().navigate(R.id.action_start_chat)
                } else {
                    showError()
                }
            }
        }
    }

    private fun showError() {
//        binding.locationErrorMessage.visibility = View.VISIBLE
//        binding.grantPermissionButton.visibility = View.VISIBLE
    }

    private fun checkLocationPermission(): Boolean {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            // Navigate to the chat fragment
//            findNavController().navigate(R.id.action_start_chat)
            return true;
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
        return false
    }


}