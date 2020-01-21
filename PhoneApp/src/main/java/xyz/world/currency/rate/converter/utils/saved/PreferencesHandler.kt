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
                this?.putString("LastCurrency", valueToSave)
                this?.commit()
            }
        }

        /**
         * Get selected currency code by user.
         */
        fun readSaveCurrency() : String {
            return sharedPreferences.getString("LastCurrency", "EUR")!!
        }

    }
}