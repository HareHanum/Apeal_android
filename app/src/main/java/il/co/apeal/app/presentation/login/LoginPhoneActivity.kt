package il.co.apeal.app.presentation.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import il.co.apeal.app.R
import il.co.apeal.app.data.RetrofitManager
import il.co.apeal.app.database.PrefUtils
import il.co.apeal.app.objects.PhoneNumber
import il.co.apeal.app.objects.SmsCode
import il.co.apeal.app.web.WebActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_phone_content.*
import kotlinx.android.synthetic.main.bottom_sheet_phone.*


class LoginPhoneActivity : AppCompatActivity(), LoginContract.View {
    private var presenter: LoginContract.Presenter = LoginPresenter()
    var dragged: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_phone)
        if (!PrefUtils.getSharedPreferences(this).getBoolean("from_web", true)) {
            if (PrefUtils.isNotNull(this, "refresh_token", null)) {
                startActivity(Intent(this, WebActivity::class.java))
            }
        }

        continue_Btn.setOnClickListener {
            continue_Btn.isEnabled = false
            countryCodePicker.registerCarrierNumberEditText(phone_ET)
            if (presenter.validate(countryCodePicker)) {
                val phoneNumber = PhoneNumber(countryCodePicker.fullNumber)
                phoneProgressBar.visibility = View.VISIBLE
                register(phoneNumber)
                hideKeyboard(this)
            } else {
                handleError()
            }
        }

        phone_ET.setOnKeyListener(View.OnKeyListener { p0, p1, p2 ->
            if (p2 != null) {
                if ((p2.action == KeyEvent.ACTION_DOWN) && (p1 == KeyEvent.KEYCODE_ENTER)) {
                    continue_Btn.isEnabled = false
                    countryCodePicker.registerCarrierNumberEditText(phone_ET)
                    if (presenter.validate(countryCodePicker)) {
                        val phoneNumber = PhoneNumber(countryCodePicker.fullNumber)
                        phoneProgressBar.visibility = View.VISIBLE
                        register(phoneNumber)
                        hideKeyboard(this)
                    } else {
                        handleError()
                    }
                    return@OnKeyListener true
                }
            }
            false
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_phone)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
                val slideUp = AnimationUtils.loadAnimation(this@LoginPhoneActivity, R.anim.slide_up)
                val slideDown = AnimationUtils.loadAnimation(this@LoginPhoneActivity, R.anim.slide_down)

                when (p1) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (!dragged) {
                            slideDown.fillAfter = true
                            login_phone_layout.startAnimation(slideDown)
                            disableViews(false)
                            arrow_Btn_phone.startAnimation(rotateImage(true))
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (!dragged) {
                            slideUp.fillAfter = true
                            login_phone_layout.startAnimation(slideUp)
                            disableViews(true)
                            countryCodePicker.detectSIMCountry(true)
                            arrow_Btn_phone.startAnimation(rotateImage(false))
                        }
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        dragged = true
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        dragged = false
                    }
                }
            }

        })

        bottom_sheet_phone_header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }


        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
    }

    override fun handleError() {
        continue_Btn.isEnabled = true
        Toast.makeText(this, resources.getString(R.string.phone_number_invalid), Toast.LENGTH_SHORT).show()
    }

    private fun register(phoneNumber: PhoneNumber) {
        RetrofitManager.shared().registerUser(phoneNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            startActivity(Intent(this, LoginSmsActivity::class.java).putExtra("phoneNumber", phoneNumber.phoneNumber))
                            phoneProgressBar.visibility = View.GONE
                        }
                ) {
                    Toast.makeText(this, phoneNumber.phoneNumber + " error", Toast.LENGTH_SHORT).show()
                    continue_Btn.isEnabled = true
                    phoneProgressBar.visibility = View.GONE
                }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun rotateImage(boolean: Boolean): RotateAnimation {
        val fromDegree: Float = if (boolean) 0f else 180f
        val toDegree: Float = if (boolean) 180f else 360f
        val rotate = RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.interpolator = LinearInterpolator()
        rotate.fillAfter = true
        return rotate
    }

    fun disableViews(boolean: Boolean) {
        countryCodePicker.isClickable = boolean
        phone_ET.isEnabled = boolean
        continue_Btn.isEnabled = boolean
    }

}





