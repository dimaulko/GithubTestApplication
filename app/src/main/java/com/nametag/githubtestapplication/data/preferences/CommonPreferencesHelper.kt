package com.nametag.githubtestapplication.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.nametag.githubtestapplication.R

class CommonPreferencesHelper(context: Context) : BasePreferencesHelper {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.system_preferences_file_name),
        Context.MODE_PRIVATE
    )

    /**
     * Method for getting boolean value from prefs
     *
     * @return boolean value
     */
    override fun getBooleanFromPrefs(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    /**
     * Method for setting boolean value from prefs
     */
    override fun setBooleanToPrefs(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Method for getting string value from prefs
     *
     * @return string value
     */
    override fun getStringFromPrefs(key: String): String? {
        return preferences.getString(key, "")
    }

    /**
     * Method for setting string value from prefs
     */
    override fun setStringToPrefs(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun setStringToPrefsCommit(key: String, value: String) {
        preferences.edit().putString(key, value).commit()
    }

    /**
     * Method for getting string value from prefs with default value
     *
     * @return string value
     */
    override fun getStringFromPrefs(key: String, defaultValue: String?): String? {
        return preferences.getString(key, defaultValue)
    }

    /**
     * Method for setting long value from prefs
     */
    override fun setLongToPrefs(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    override fun setLongToPrefsCommit(key: String, value: Long) {
        preferences.edit().putLong(key, value).commit()
    }

    /**
     * Method for getting long value from prefs
     *
     * @return long value
     */
    override fun getLongFromPrefs(key: String): Long {
        return preferences.getLong(key, 0)
    }

    /**
     * Method for getting int value from prefs
     *
     * @return integer value
     */
    override fun getIntFromPrefs(key: String): Int {
        return preferences.getInt(key, 0)
    }

    /**
     * Method for setting int value from prefs
     */
    override fun setIntToPrefs(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    override fun removeAllData() {
        preferences.edit().clear().commit()
    }

}