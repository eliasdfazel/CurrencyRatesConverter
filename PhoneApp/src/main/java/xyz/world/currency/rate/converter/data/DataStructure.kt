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
 * Parse JSON Objects for Downloading Rates.
 * "success": ,
 * "terms": ,
 * "privacy": ,
 * "timestamp": ,
 * "source": ,
 * "quotes":
 * */
class RatesJsonDataStructure {

    companion object {
        const val SUCCESS = "success"
        const val SOURCE = "source"
        const val TIMESTAMP = "timestamp"
        const val QUOTES = "quotes"
    }
}

/**
 * Data Class to Parse JSON Objects for Supported Currencies List.
 * "success": ,
 * "terms": ,
 * "privacy": ,
 * "timestamp": ,
 * "source": ,
 * "quotes":
 * */
data class SupportedListJsonResultResult(val success: Boolean, val terms: String, val privacy: String, val currencies: HashMap<String, String>)