package xyz.world.currency.rate.converter.data.room

class DatabasePath {

    companion object {
        const val CURRENCY_DATABASE = "CurrencyRates"

        val CREATE_DATABASE_TABLE = ("CREATE TABLE IF NOT EXISTS "
                + DatabasePath.CURRENCY_DATABASE +
                "(" + "CurrencyCode" + " TEXT PRIMARY KEY, FullCurrencyName TEXT, CurrencyRate REAL, LastUpdateTime TEXT)")
    }
}