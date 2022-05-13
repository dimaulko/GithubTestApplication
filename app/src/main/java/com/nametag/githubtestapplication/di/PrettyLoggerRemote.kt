package com.nametag.githubtestapplication.di

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor

class PrettyLoggerRemote : HttpLoggingInterceptor.Logger {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun log(message: String) {
        val trimMessage = message.trim { it <= ' ' }
        if (trimMessage.startsWith("{") && trimMessage.endsWith("}")
            || trimMessage.startsWith("[") && trimMessage.endsWith("]")
        ) {
            try {
                val prettyJson: String = gson.toJson(message)
                Platform.get().log(prettyJson, Log.INFO, null)
            } catch (e: Exception) {
                Platform.get().log(message, Log.WARN, e)
            }
        } else {
            Platform.get().log(message, Log.INFO, null)
        }
    }
}