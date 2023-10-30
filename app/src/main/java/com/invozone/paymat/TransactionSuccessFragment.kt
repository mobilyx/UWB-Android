package com.invozone.paymat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.invozone.paymat.databinding.FragmentTransactionSuccessBinding


class TransactionSuccessFragment : Fragment(R.layout.fragment_transaction_success) {

    private var amount: String?=""
    private var isReceiver: Boolean? = null
    private var binding: FragmentTransactionSuccessBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionSuccessBinding.bind(view)
        isReceiver = arguments?.getBoolean("is_receiver")
        amount = arguments?.getString("amount")
        if (isReceiver!!){
            binding?.moneyStatus?.text = "Money Received"
            binding?.amount?.text = "${"Rs: "}${amount.toString()}"
        }else{
            binding?.moneyStatus?.text = "Money Transferred"
            binding?.amount?.text = "${"Rs: "}${amount.toString()}"
        }
    }

}