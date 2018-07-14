package il.co.apeal.app.presentation.login

import com.hbb20.CountryCodePicker
import il.co.apeal.app.MVP.ViewPresenterContract
import il.co.apeal.app.objects.PhoneNumber

class LoginContract : ViewPresenterContract {
    interface View: ViewPresenterContract.View {
        fun handleError()

    }

    interface Presenter: ViewPresenterContract.Presenter<View> {
        fun validate(countryCodePicker: CountryCodePicker): Boolean
        fun getVerificationCode(phoneNumber: String)


    }
}