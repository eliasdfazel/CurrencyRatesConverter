package xyz.world.currency.rate.converter.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.currency_list_items.view.*

/**
 * All Child Views Used for RecyclerView Items
 */
class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    /**
     * The Full View Of One Item (Row) in RecyclerView
     */
    val mainView: ConstraintLayout = view.mainView
    val currencyRate: TextView = view.currencyRate
    val countryFlag: ImageView = view.countryFlag
    val currencyName: TextView = view.currencyName
    val currencyCountry: TextView = view.currencyCountry
}