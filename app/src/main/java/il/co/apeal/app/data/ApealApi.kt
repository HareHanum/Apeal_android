package il.co.apeal.app.data

import il.co.apeal.app.objects.PhoneNumber
import il.co.apeal.app.objects.SmsCode
import il.co.apeal.app.objects.Token
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApealApi {

    @POST("/api/Account/Register")
    fun register(@Body number: PhoneNumber): Observable<ResponseBody>

    @POST("/api/Account/VerifyPhoneNumber")
    fun verifyPhoneNumber(@Body code: SmsCode): Observable<ResponseBody>

    @POST("/Token")
    fun getToken(@Body request: RequestBody): Observable<Token>


}