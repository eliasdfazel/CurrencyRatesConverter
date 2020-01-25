package xyz.world.currency.rate.converter.data.database

class DatabasePath {

    companion object {
        /**
         *  Directory to Firebase Firestore Database.
         *
         *  It is to get additional information about currencies; Full Currency Name with Country Name.
         */
        const val FIRESTORE_REFERENCE_DIRECTORY = "Currency/Public/Rates/"

        /**
         *  Database File Name & Main Database Table Name.
         */
        const val CURRENCY_DATABASE_NAME = "CurrencyRates"

        /**
         *  SQL Command to Create Table & Its Column.
         */
        val CREATE_DATABASE_TABLE_COMMAND = ("CREATE TABLE IF NOT EXISTS "
                + DatabasePath.CURRENCY_DATABASE_NAME +
                "(" + "CurrencyCode" + " TEXT PRIMARY KEY, CountryName TEXT, CurrencyRate REAL, LastUpdateTime TEXT)")
    }
}