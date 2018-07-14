package il.co.apeal.app.presentation.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import il.co.apeal.app.R
import il.co.apeal.app.constants.Constants
import il.co.apeal.app.data.RetrofitManager
import il.co.apeal.app.database.PrefUtils
import il.co.apeal.app.objects.SmsCode
import il.co.apeal.app.web.WebActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_sms_content.*
import kotlinx.android.synthetic.main.bottom_sheet_sms.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*


class LoginSmsActivity : AppCompatActivity() {

    private var phoneNumber: String = ""
    private val password: String = randomString()
    private val REQUEST_CODE: Int = 123
    var dragged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_sms)
        phoneNumber = intent.getStringExtra("phoneNumber")
        if (!isSmsPermissionGranted()) {
            requestReadAndSendSmsPermission()
        } else {

        }

        continue_Btn.setOnClickListener {
            continue_Btn.isEnabled = false
            smsProgressBar.visibility = View.VISIBLE
            if (!pinEntryEditText.text.isEmpty()) {
                val smsCode = SmsCode(pinEntryEditText.text.toString().toInt(), phoneNumber, password)
                validateSmsCode(smsCode)
                hideKeyboard(this)
            } else {
                smsProgressBar.visibility = View.GONE
                continue_Btn.isEnabled = true
                Toast.makeText(this, "הזן את הקוד", Toast.LENGTH_SHORT).show()
            }

        }

        pinEntryEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length == 6) {
                    continue_Btn.isEnabled = false
                    hideKeyboard(this@LoginSmsActivity)
                    smsProgressBar.visibility = View.VISIBLE
                    val smsCode = SmsCode(pinEntryEditText.text.toString().toInt(), phoneNumber, password)
                    validateSmsCode(smsCode)


                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_sms)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
                val slideUp = AnimationUtils.loadAnimation(this@LoginSmsActivity, R.anim.slide_up)
                val slideDown = AnimationUtils.loadAnimation(this@LoginSmsActivity, R.anim.slide_down)
                when (p1) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (!dragged) {
                            slideDown.fillAfter = true
                            login_sms_layout.startAnimation(slideDown)
                            disableViews(false)
                            arrow_Btn_sms.startAnimation(rotateImage(true))
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (!dragged) {
                            slideUp.fillAfter = true
                            login_sms_layout.startAnimation(slideUp)
                            disableViews(true)
                            arrow_Btn_sms.startAnimation(rotateImage(false))
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

        bottom_sheet_sms_header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


    }

    private fun validateSmsCode(smsCode: SmsCode) {
        RetrofitManager.shared().SmsCodevalidation(smsCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            continue_Btn.isEnabled = false
                            val requestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), formatURL())
                            getToken(requestBody)
                        }
                ) {
                    continue_Btn.isEnabled = true
                    Toast.makeText(this, resources.getString(R.string.sms_code_invalid), Toast.LENGTH_SHORT).show()
                    smsProgressBar.visibility = View.GONE
                }
    }

    private fun getToken(requestBody: RequestBody) {
        RetrofitManager.shared().getToken(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            continue_Btn.isEnabled = false
                            smsProgressBar.visibility = View.GONE
                            PrefUtils.insertString(this, "refresh_token", it.refreshToken)
                            startActivity(Intent(this, WebActivity::class.java))
                            finish()
                        }
                ) {
                    continue_Btn.isEnabled = true
                    smsProgressBar.visibility = View.GONE
                    Toast.makeText(this, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginSmsActivity::class.java))
                    finish()
                }
    }


    private fun randomString(): String {
        val s1 = UUID.randomUUID().toString()
        val s2 = s1.replace("-", "0")
        return s2.substring(1, 10)
    }

    private fun formatURL(): String {
        val s1 = Constants().GET_TOKEN_STRING
        val s2 = s1.replace("phoneNumber", phoneNumber)
        return s2.replace("userPassword", password)
    }

    fun hideKeyboard(activity: Activity) {
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
        pinEntryEditText.isEnabled = boolean
        continue_Btn.isEnabled = boolean
    }

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    smsBroadcastReceiver = SmsBroadcastReceiver()
//                    registerReceiver(smsBroadcastReceiver,IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }
}
