package il.co.apeal.app.data

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import il.co.apeal.app.constants.Constants
import il.co.apeal.app.objects.PhoneNumber
import il.co.apeal.app.objects.SmsCode
import il.co.apeal.app.objects.Token
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    private var apealApi: ApealApi? = null


    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants().API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        apealApi = retrofit.create(ApealApi::class.java)
    }



    companion object {
        private var instance : RetrofitManager? = null


        fun shared(): RetrofitManager {
            if (instance == null)
                instance = RetrofitManager()

            return instance!!
        }
    }

    fun registerUser(phoneNumber: PhoneNumber): Observable<ResponseBody> {
        return apealApi!!.register(phoneNumber)
    }


    fun getToken(requestBody: RequestBody): Observable<Token> {
        return apealApi!!.getToken(requestBody)
    }

    fun SmsCodevalidation(smsCode: SmsCode): Observable<ResponseBody> {
        return apealApi!!.verifyPhoneNumber(smsCode)
    }


}
