package com.nametag.githubtestapplication.data.preferences

interface BasePreferencesHelper {
    fun getBooleanFromPrefs(key: String): Boolean
    fun setBooleanToPrefs(key: String, value: Boolean)
    fun getStringFromPrefs(key: String): String?
    fun setStringToPrefs(key: String, value: String)
    fun setStringToPrefsCommit(key: String, value: String)
    fun getStringFromPrefs(key: String, defaultValue: String?): String?
    fun setLongToPrefs(key: String, value: Long)
    fun setLongToPrefsCommit(key: String, value: Long)
    fun getLongFromPrefs(key: String): Long
    fun getIntFromPrefs(key: String): Int
    fun setIntToPrefs(key: String, value: Int)
    fun removeAllData()
}