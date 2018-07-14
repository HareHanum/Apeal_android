package il.co.apeal.app.database

import android.content.Context
import android.content.SharedPreferences

class PrefUtils {

    companion object {
        fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
        }

        fun insertString(context: Context, key: String, value: String?) {
            val editor = getSharedPreferences(context).edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun insertBoolean(context: Context, key: String, value: Boolean) {
            val editor = getSharedPreferences(context).edit()
            editor.putBoolean(key,value)
            editor.apply()
        }

        fun deleteString(context: Context, key: String, value: String) {
            val editor = getSharedPreferences(context).edit()
            editor.remove(key)
            editor.apply()
        }

        fun isNotNull(context: Context, key: String, value: String?): Boolean {
            if (getSharedPreferences(context).getString(key, value) == null ||
                    getSharedPreferences(context).getString(key, value) == "") {
                return false
            }
            return true
        }
    }


}