package xyz.learn.world.heritage.SavedData

import android.content.Context

internal class PreferencesHandler(var context: Context) {


    inner class CurrencyPreferences() {

        private val sharedPreferences = context.getSharedPreferences("CurrencyPreferences", Context.MODE_PRIVATE)

        /**
         * CurrencyAPI Free AccessKey Does NOT Support Source Currency Change. The Default Source is USD.
         * So, I calculate approx Rate Offset based on selected currency exchange rate with USD.
         */
        fun saveRateOffset(valueToSave: String?) {
            with (sharedPreferences.edit()) {
                this?.putString("RateOffset", valueToSave)
                this?.commit()
            }
        }

        /**
         * CurrencyAPI Free AccessKey Does NOT Support Source Currency Change. The Default Source is USD.
         * So, I calculate approx Rate Offset based on selected currency exchange rate with USD.
         */
        fun readRateOffset() : String {
            return sharedPreferences.getString("RateOffset", "1.0")!!
        }

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


        /**
         *  Save Time in Millisecond for the last update of currencies rates.
         */
        fun saveLastRatesUpdate(valueToSave: Long) {
            with (sharedPreferences.edit()) {
                this?.putLong("LastRatesUpdate", (valueToSave))
                this?.commit()
            }
        }

        /**
         *  Read Time in Millisecond for the last update of currencies rates.
         */
        fun readLastRatesUpdate() : Long {
            return sharedPreferences.getLong("LastRatesUpdate", 0)
        }

        /**
         *  Save Last Time List Of Support Currencies Updated.
         */
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