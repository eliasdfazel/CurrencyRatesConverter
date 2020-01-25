package xyz.world.currency.rate.converter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.entry_configurations.*
import xyz.world.currency.rate.converter.ui.CurrencyList
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints

class EntryConfigurations : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_configurations)

        val systemCheckpoints = SystemCheckpoints(context = applicationContext)

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //Network Connection Listener when app launched in offline device.
        val networkConnectivityCallBack = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                runOnUiThread {
                    if (systemCheckpoints.networkConnection()) {

                        supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, 0)
                            .replace(R.id.fragmentConverterRate, CurrencyList(), "Currency List")
                            .commit()

                        connectivityManager.unregisterNetworkCallback(this)
                    }
                }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkConnectivityCallBack)

        if (!systemCheckpoints.networkConnection()) {
            toolbarOption.visibility = View.VISIBLE

            window.statusBarColor = Color.BLACK
            window.navigationBarColor = Color.BLACK
        }

        window.statusBarColor = getColor(R.color.colorPrimaryDark)
        window.navigationBarColor = getColor(R.color.colorPrimaryDark)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onBackPressed() {
        val homeScreen = Intent(Intent.ACTION_MAIN).apply {
            this.addCategory(Intent.CATEGORY_HOME)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeScreen, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
    }
}
