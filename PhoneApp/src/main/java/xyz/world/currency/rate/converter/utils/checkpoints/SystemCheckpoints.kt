package xyz.world.currency.rate.converter.utils.checkpoints

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class SystemCheckpoints(var context: Context) {

    /**
     * Check if Device is Connected to a Network.
     */
    fun networkConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                true
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                true
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                true
            }
            else -> false
        }
    }
}