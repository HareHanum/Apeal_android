package il.co.apeal.app.objects

import com.google.gson.annotations.SerializedName

open class Token constructor(@SerializedName("access_token")
                             var accessToken: String,
                             @SerializedName("token_type")
                             var tokenType: String,
                             @SerializedName("expires_in")
                             var expiresIn: Int,
                             @SerializedName("refresh_token")
                             var refreshToken: String,
                             @SerializedName("userName")
                             var userName: String,
                             @SerializedName(".issued")
                             var issued: String,
                             @SerializedName(".expires")
                             var expires: String)

