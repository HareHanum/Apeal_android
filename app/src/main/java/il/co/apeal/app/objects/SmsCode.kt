package il.co.apeal.app.objects

import com.google.gson.annotations.SerializedName

open class SmsCode constructor(@SerializedName("Code") val code: Int,
                               @SerializedName("phoneNumber") val phoneNumber: String,
                               @SerializedName("NewPassword") val password: String)