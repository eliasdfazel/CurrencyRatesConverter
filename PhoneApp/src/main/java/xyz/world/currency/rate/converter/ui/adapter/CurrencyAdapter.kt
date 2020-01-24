package xyz.world.currency.rate.converter.ui.adapter

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.R
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.data.database.DatabasePath
import xyz.world.currency.rate.converter.data.download.rates.UpdateCurrenciesRatesData
import xyz.world.currency.rate.converter.utils.extensions.formatToThreeDigitAfterPoint
import xyz.world.currency.rate.converter.utils.saved.CountryData

class CurrencyAdapter(var context: Context) : RecyclerView.Adapter<ItemViewHolder>() {

    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var recyclerView: RecyclerView? = null

    var recyclerViewItemsDataStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()

    /**
     * CurrencyAPI Free AccessKey Does NOT Support Source Currency Change. The Default Source is USD.
     * So, I calculate approx Rate Offset based on selected currency exchange rate with USD.
     */
    var rateOffset: Double = PreferencesHandler(context).CurrencyPreferences().readRateOffset().toDouble()
    var multiplyNumber: Double = 1.0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {

        return ItemViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.currency_list_items,
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount(): Int {

        return recyclerViewItemsDataStructure.size
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            Log.d("PayLoads Update", "${recyclerViewItemsDataStructure[position].currencyCode} ::: ${recyclerViewItemsDataStructure[position].currencyRate}")

            itemViewHolder.currencyRate.text = (recyclerViewItemsDataStructure[position].currencyRate.times((multiplyNumber * rateOffset)).formatToThreeDigitAfterPoint())
            itemViewHolder.currencyName.text = recyclerViewItemsDataStructure[position].currencyCode
            firestore.document(DatabasePath.FIRESTORE_REFERENCE_DIRECTORY + recyclerViewItemsDataStructure[position].currencyCode).get().addOnSuccessListener {
                val countryName = it.getString("CountryName")

                itemViewHolder.currencyCountry.text = if (countryName.isNullOrEmpty()) { context.getString(R.string.threeDot) } else { countryName }
            }

            Glide.with(context)
                .load(CountryData().flagCountryLink(recyclerViewItemsDataStructure[position].currencyCode.toLowerCase()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(itemViewHolder.countryFlag)
        } else {
            super.onBindViewHolder(itemViewHolder, position, payloads)
        }
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
        Log.d("Full Update", "${recyclerViewItemsDataStructure[position].currencyCode} ::: ${recyclerViewItemsDataStructure[position].currencyRate}")

        itemViewHolder.currencyRate.text = ((recyclerViewItemsDataStructure[position].currencyRate.times(multiplyNumber)).formatToThreeDigitAfterPoint())
        itemViewHolder.currencyName.text = recyclerViewItemsDataStructure[position].currencyCode
        firestore.document(DatabasePath.FIRESTORE_REFERENCE_DIRECTORY + recyclerViewItemsDataStructure[position].currencyCode).get().addOnSuccessListener {
            val countryName = it.getString("CountryName")

            itemViewHolder.currencyCountry.text = if (countryName.isNullOrEmpty()) { context.getString(R.string.threeDot) } else { countryName }
        }

        Glide.with(context)
            .load(CountryData().flagCountryLink(recyclerViewItemsDataStructure[position].currencyCode.toLowerCase()))
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(itemViewHolder.countryFlag)

        itemViewHolder.mainView.setOnClickListener {
            Log.d("Base Currency", itemViewHolder.currencyName.text.toString())

            UpdateCurrenciesRatesData.CONTINUE_UPDATE_SUBSCRIPTION = false


            multiplyNumber = itemViewHolder.currencyRate.text.toString().toDouble()
            Log.d("Base Rate Multiplier", "${multiplyNumber}")


            PreferencesHandler(context).CurrencyPreferences().saveLastCurrency(itemViewHolder.currencyName.text.toString())
            CurrencyDataViewModel.baseCurrency.postValue(itemViewHolder.currencyName.text.toString())

            //Change [CONTINUE_UPDATE_SUBSCRIPTION] to allow flow continue.
            Handler().postDelayed({
                UpdateCurrenciesRatesData.CONTINUE_UPDATE_SUBSCRIPTION = true
            }, 1500)
        }
    }
}