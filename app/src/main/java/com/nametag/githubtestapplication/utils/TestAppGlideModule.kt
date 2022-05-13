package com.nametag.githubtestapplication.utils

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class TestAppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.DEBUG)
        super.applyOptions(context, builder)
    }
}
