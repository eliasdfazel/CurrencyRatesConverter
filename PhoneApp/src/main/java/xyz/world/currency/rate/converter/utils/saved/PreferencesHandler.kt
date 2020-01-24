package xyz.learn.world.heritage.SavedData

import android.content.Context

internal class PreferencesHandler(var context: Context) {


    inner class CurrencyPreferences() {

        private val sharedPreferences = context.getSharedPreferences("CurrencyPreferences", Context.MODE_PRIVATE)

        /**
         * It will save currency code when user click on item on list.
         */
        fun saveLastCurrency(valueToSave: String?) {
            with (sharedPreferences.edit()) {
                this?.putString("LastSelectedCurrency", valueToSave)
                this?.commit()
            }
        }

        /**
         * Get selected currency code by user.
         */
        fun readSaveCurrency() : String {
            return sharedPreferences.getString("LastSelectedCurrency", "USD")!!
        }

        fun saveLastRatesUpdate(valueToSave: Long) {
            with (sharedPreferences.edit()) {
                this?.putLong("LastRatesUpdate", (valueToSave))
                this?.commit()
            }
        }

        fun readLastRatesUpdate() : Long {
            return sharedPreferences.getLong("LastRatesUpdate", 0)
        }

        fun saveLastCurrencyListUpdate(valueToSave: Long) {
            with (sharedPreferences.edit()) {
                this?.putLong("LastCurrencyListUpdate", (valueToSave))
                this?.commit()
            }
        }

        fun readLastCurrencyListUpdate() : Long {
            return sharedPreferences.getLong("LastCurrencyListUpdate", 0)
        }
    }
}