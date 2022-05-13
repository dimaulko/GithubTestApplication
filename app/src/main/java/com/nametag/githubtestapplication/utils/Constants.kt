package com.nametag.githubtestapplication.di

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nametag.githubtestapplication.TestApplication

const val API_URL_PROD = "https://api.github.com/"

fun <T : Any?> LifecycleOwner.subscribe(liveData: LiveData<T>, action: (T) -> Unit) {
    liveData.observe(this, Observer<T> { t -> t?.run { action.invoke(this) } })
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

@Px
fun dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
    val resources: Resources = TestApplication.instance.resources
    val displayMetrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics)
        .toInt()
}
fun hideKeyboard(context: Context) {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if ((context as Activity).currentFocus != null) {
        try {
            inputManager.hideSoftInputFromWindow(
                (context as AppCompatActivity).currentFocus!!.windowToken,
                0
            )
        } catch (ex: NullPointerException) {
            //
        }
    }
}