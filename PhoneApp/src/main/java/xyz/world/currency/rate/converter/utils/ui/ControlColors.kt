package xyz.world.currency.rate.converter.utils.ui

import android.os.Build
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import xyz.world.currency.rate.converter.R

/**
 * Change NavBar and StatusBar Color after Splash Screen.
 */
fun setupUI(activity: FragmentActivity) {
    //Changing UI After Splash Screen
    activity.findViewById<ConstraintLayout>(R.id.mainView)?.setBackgroundColor(activity.getColor(R.color.mainColor))

    activity.window!!.statusBarColor = activity.getColor(R.color.mainColor)

    activity.window!!.navigationBarColor = if (Build.VERSION.SDK_INT > 25) {
        activity.getColor(R.color.mainColor)
    } else {
        activity.getColor(R.color.colorPrimaryDark)
    }

    activity.window!!.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT > 25) {
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    } else {
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}