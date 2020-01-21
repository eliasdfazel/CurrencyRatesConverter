package xyz.world.currency.rate.converter.data

class DatabasePath {

    companion object {
        /* Currency/{UID}/Rates/{NameOfCurrency} */
        /**
         * Firestore Path to Collection Of Currency Rates
         * Currency/Public Directory is for saving some common data.
         *
         * Currency/{UID} Directory is specific for a user.
         */
        var FIRESTORE_REFERENCE_DIRECTORY = "Currency/Public/Rates/"

        const val CURRENCY_DATABASE = ""
    }
}