package il.co.apeal.app.objects

import com.google.gson.annotations.SerializedName

open class PhoneNumber constructor(@SerializedName("Number") val phoneNumber: String)

