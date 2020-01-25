package xyz.world.currency.rate.converter.utils.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.R
import xyz.world.currency.rate.converter.utils.saved.CountryData

fun downloadAndSetFlagImage(context: Context, imageView: ImageView) {
    Glide.with(context)
        .load(CountryData().flagCountryLink(PreferencesHandler(context).CurrencyPreferences().readSaveCurrency().toLowerCase()))
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                e?.printStackTrace()

                imageView.setImageDrawable(context.getDrawable(R.drawable.currency_symbols_icon))

                return true
            }

            override fun onResourceReady(drawable: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                imageView.setImageDrawable(drawable)

                return true
            }
        })
        .submit()
}