package xyz.world.currency.rate.converter.data

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
data class DataStructureJsonResultResult(val success: Boolean, val source: String, val timestamp: Long, val quotes: Map<String, Double>)