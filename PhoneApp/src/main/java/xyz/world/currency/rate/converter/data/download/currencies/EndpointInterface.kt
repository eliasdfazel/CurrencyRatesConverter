package xyz.world.currency.rate.converter.data

import io.reactivex.Single
import retrofit2.http.GET

interface EndpointInterface {

    //http://api.currencylayer.com/list?access_key=3057d94042a99324b134799aa7c252f9
    companion object {
        private const val API_KEY: String = "3057d94042a99324b134799aa7c252f9"
        val BASE_Link = "http://api.currencylayer.com/"
    }
    /**
     * Async Call to retrieve data from Public API, BASE_LINK.
     * with suffix parameter of Base Currency.
     */
    @GET("/list?access_key=${EndpointInterface.API_KEY}")
    fun getListOfSupportCurrencies(): Single<SupportedListJsonResultResult>
}