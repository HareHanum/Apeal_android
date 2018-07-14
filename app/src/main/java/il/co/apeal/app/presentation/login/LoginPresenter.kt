package il.co.apeal.app.presentation.login

import com.hbb20.CountryCodePicker
import il.co.apeal.app.MVP.BasePresenter

class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {
    override fun validate(countryCodePicker: CountryCodePicker): Boolean {
        if (countryCodePicker.isValidFullNumber) {
            return true

        }
        return false
    }

    override fun getVerificationCode(phoneNumber: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}