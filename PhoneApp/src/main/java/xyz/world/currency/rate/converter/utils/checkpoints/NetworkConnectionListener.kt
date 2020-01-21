package xyz.world.currency.rate.converter.utils.checkpoints

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.entry_configurations.*
import xyz.world.currency.rate.converter.R

class NetworkConnectionListener(var activity: FragmentActivity) :  ConnectivityManager.NetworkCallback() {

    val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        connectivityManager.registerDefaultNetworkCallback(this@NetworkConnectionListener)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)

        activity.runOnUiThread {
            if (SystemCheckpoints(activity).networkConnection()) {
                activity.toolbarOption.setImageDrawable(activity.getDrawable(R.drawable.refresh_icon))

            }
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)

        activity.runOnUiThread {
            if (SystemCheckpoints(activity).networkConnection()) {
                activity!!.toolbarOption.setImageDrawable(activity.getDrawable(R.drawable.no_internet))

            }
        }
    }
}