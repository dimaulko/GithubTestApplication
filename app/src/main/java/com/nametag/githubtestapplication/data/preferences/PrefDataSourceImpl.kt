package com.nametag.githubtestapplication.data.preferences

import android.content.Context

class PrefDataSourceImpl(val context: Context) : IPrefDataSource {
    private val commonPreferencesHelper: CommonPreferencesHelper = CommonPreferencesHelper(context) // copy/paste :)
}
