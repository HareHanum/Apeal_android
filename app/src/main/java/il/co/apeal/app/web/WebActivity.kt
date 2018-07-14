package il.co.apeal.app.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import il.co.apeal.app.R
import il.co.apeal.app.constants.Constants
import il.co.apeal.app.database.PrefUtils
import il.co.apeal.app.presentation.login.LoginPhoneActivity
import kotlinx.android.synthetic.main.activity_web.*


class WebActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        setSupportActionBar(web_toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            val toggle = ActionBarDrawerToggle(this@WebActivity, drawerLayout, web_toolbar, R.string.app_name, R.string.app_name)
            toggle.isDrawerIndicatorEnabled = true
            toggle.syncState()
            toggle.drawerArrowDrawable.color = Color.WHITE
        }

        webView.settings.javaScriptEnabled = true

        val token = PrefUtils.getSharedPreferences(this).getString("refresh_token", null)
        val builder = Uri.Builder()
        builder.appendQueryParameter("token", token)


        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                PrefUtils.insertBoolean(this@WebActivity, "from_web", true)
                Toast.makeText(this@WebActivity, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@WebActivity, LoginPhoneActivity::class.java))
                finish()

            }

        }
        webView.loadUrl(Constants().WEB_URL + builder.build().toString())

        navigationDrawer.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.logOut -> {
                    PrefUtils.deleteString(this, "refresh_token", "refresh_token")
                    PrefUtils.insertBoolean(this, "from_web", true)
                    startActivity(Intent(this, LoginPhoneActivity::class.java))
                    finish()
                }
            }


            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
