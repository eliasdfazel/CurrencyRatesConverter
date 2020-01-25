package xyz.world.currency.rate.converter.data.download.rates

/**
 *  Endpoint Address for Volley Process.
 */
class Endpoint {
    companion object {
        private val API_KEY: String = "3057d94042a99324b134799aa7c252f9"
        val BASE_Link = "http://api.currencylayer.com/live?access_key=${API_KEY}&source="
    }
}