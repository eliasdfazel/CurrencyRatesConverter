package xyz.world.currency.rate.converter.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface EndpointInterface {

    companion object {
        const val BASE_Link = "https://revolut.duckdns.org"
    }

    /**
     * Async Call to retrieve data from Public API, BASE_LINK.
     * with suffix parameter of Base Currency.
     */
    @GET("/latest?base=")
    fun downloadRatesData(@Query("base") base: String): Single<DataStructureJsonResultResult>
}