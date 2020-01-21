package xyz.world.currency.rate.converter.data

class ItemsDataStructure {

    var countryName: String? = null

    var currencyCode: String? = null
    var currencyRate: Double? = 1.0

    /**
     *  ItemsDataStructure for Public API Call.
     *  Country Name will download from Public Firestore Database Currency/Public/Rates/{CurrencyName}/CountryName
     */
    constructor(CurrencyCode: String?, Rate: Double?) {
        this.currencyCode= CurrencyCode

        this.currencyRate = Rate
    }
}

/**
 * Data Class to Parse JSON Objects from Revolut.Duckdns.org
 */
data class ItemsDataStructureJsonResultResult(val base: String, val date: String, val rates: Map<String, Double>)