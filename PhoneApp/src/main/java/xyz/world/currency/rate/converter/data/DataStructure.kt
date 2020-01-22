package xyz.world.currency.rate.converter.data

class RoomDatabaseColumn {

    companion object {
        const val CurrencyCode = "CurrencyCode"
        const val CountryName = "CountryName"
        const val CurrencyRate = "CurrencyRate"
        const val LastUpdateTime = "LastUpdateTime"
    }
}

/**
 *  ItemsDataStructure for Public API Call.
 *  Country Name will download from Public Firestore Database Currency/Public/Rates/{CurrencyName}/CountryName
 */
data class RecyclerViewItemsDataStructure (var currencyCode: String, var currencyRate: Double)

/**
 * Data Class to Parse JSON Objects.
 * "success": ,
 * "terms": ,
 * "privacy": ,
 * "timestamp": ,
 * "source": ,
 * "quotes":
 * */

class JsonDataStructure {

    companion object {
        const val SUCCESS = "success"
        const val SOURCE = "source"
        const val TIMESTAMP = "timestamp"
        const val QUOTES = "quotes"
    }
}

data class SupportedListJsonResultResult(val success: Boolean, val currencies: Map<String, Double>)
