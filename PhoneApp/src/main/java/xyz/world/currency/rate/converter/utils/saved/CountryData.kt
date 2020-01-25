package xyz.world.currency.rate.converter.utils.saved

class CountryData {

    //https://firebasestorage.googleapis.com/v0/b/currencyrateconverter.appspot.com/o/Assets%2FFlags%2F[usd]_flag.png
    /**
     *  It Gets Currency Code & Create a Download Link of Flags Images in Firebase Storage.
     */
    fun flagCountryLink(currencyName: String) : String = "https://firebasestorage.googleapis.com/v0/b/currencyrateconverter.appspot.com/o/Assets%2FFlags%2F" + currencyName + "_flag.png" + "?alt=media"
}