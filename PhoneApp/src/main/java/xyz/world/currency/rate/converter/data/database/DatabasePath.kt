package xyz.world.currency.rate.converter.data.database

class DatabasePath {

    companion object {
        const val CURRENCY_DATABASE_NAME = "CurrencyRates"
        const val FIRESTORE_REFERENCE_DIRECTORY = "Currency/Public/Rates/"

        val CREATE_DATABASE_TABLE_COMMAND = ("CREATE TABLE IF NOT EXISTS "
                + DatabasePath.CURRENCY_DATABASE_NAME +
                "(" + "CurrencyCode" + " TEXT PRIMARY KEY, CountryName TEXT, CurrencyRate REAL, LastUpdateTime TEXT)")
    }
}