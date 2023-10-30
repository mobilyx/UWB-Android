package com.invozone.paymat.model


enum class TransactionStatus {
    Received,
    Pending,
    Failed,
}

data class TransactionDataModel(val name:String, val transactionStatus:TransactionStatus, val amount: Int){
    val color: String = when (transactionStatus) {
        TransactionStatus.Received -> "#159F22"
        TransactionStatus.Pending -> "#EC2727"
        else -> "#FFC82C"
    }

}
