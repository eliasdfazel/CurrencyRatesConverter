package xyz.world.currency.rate.converter.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.firestore.FirebaseFirestore
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.R
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.data.database.DatabasePath
import xyz.world.currency.rate.converter.data.download.UpdateCloudData
import xyz.world.currency.rate.converter.utils.extensions.formatToThreeDigitAfterPoint
import xyz.world.currency.rate.converter.utils.saved.CountryData

class CurrencyAdapter(var context: Context) : RecyclerView.Adapter<ItemViewHolder>() {

    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var recyclerView: RecyclerView? = null

    var recyclerViewItemsDataStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()
    var recyclerViewItemsDataStructurePayload: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()

    var updateFirstRowPayload: Boolean = false
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
            Log.d("PayLoads Update", "${recyclerViewItemsDataStructurePayload[position].currencyCode} ::: ${recyclerViewItemsDataStructurePayload[position].currencyRate}")

            itemViewHolder.currencyRate.text = (recyclerViewItemsDataStructurePayload[position].currencyRate?.times(multiplyNumber)!!.formatToThreeDigitAfterPoint())
            itemViewHolder.currencyName.text = recyclerViewItemsDataStructurePayload[position].currencyCode
            firestore.document(DatabasePath.FIRESTORE_REFERENCE_DIRECTORY + recyclerViewItemsDataStructurePayload[position].currencyCode).get().addOnSuccessListener {
                itemViewHolder.currencyCountry.text = it.getString("CountryName")
            }

            Glide.with(context)
                .load(CountryData().flagCountryLink(recyclerViewItemsDataStructurePayload[position].currencyCode!!.toLowerCase()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(itemViewHolder.countryFlag)
        } else {
            super.onBindViewHolder(itemViewHolder, position, payloads)
        }
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
        Log.d("Full Update", "${recyclerViewItemsDataStructure[position].currencyCode} ::: ${recyclerViewItemsDataStructure[position].currencyRate}")

        itemViewHolder.currencyRate.text = ((recyclerViewItemsDataStructure[position].currencyRate!!.times(multiplyNumber)).formatToThreeDigitAfterPoint())
        itemViewHolder.currencyName.text = recyclerViewItemsDataStructure[position].currencyCode
        firestore.document(DatabasePath.FIRESTORE_REFERENCE_DIRECTORY + recyclerViewItemsDataStructure[position].currencyCode).get().addOnSuccessListener {
            itemViewHolder.currencyCountry.text = it.getString("CountryName")
        }

        Glide.with(context)
            .load(CountryData().flagCountryLink(recyclerViewItemsDataStructure[position].currencyCode!!.toLowerCase()))
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(glideException: GlideException?, any: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, boolean: Boolean): Boolean { return false }

                override fun onResourceReady(drawable: Drawable?, any: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, boolean: Boolean): Boolean { return false }
            })
            .into(itemViewHolder.countryFlag)

        itemViewHolder.mainView.setOnClickListener {
            Log.d("Base Currency", itemViewHolder.currencyName.text.toString())
            UpdateCloudData.CONTINUE_UPDATE_SUBSCRIPTION = false

            PreferencesHandler(context).CurrencyPreferences().saveLastCurrency(itemViewHolder.currencyName.text.toString())

            multiplyNumber = itemViewHolder.currencyRate.text.toString().toDouble()
            Log.d("Base Rate Multiplier", "${multiplyNumber}")

            Handler().postDelayed({
                recyclerView?.smoothScrollToPosition(0)
            }, 200)

            notifyItemMoved(itemViewHolder.adapterPosition, 0)
            updateFirstRowPayload = true

            itemViewHolder.currencyRate.requestFocus()

            //Change [CONTINUE_UPDATE_SUBSCRIPTION] to allow flow continue.
            Handler().postDelayed({
                UpdateCloudData.CONTINUE_UPDATE_SUBSCRIPTION = true
            }, 1500)
        }
    }
}