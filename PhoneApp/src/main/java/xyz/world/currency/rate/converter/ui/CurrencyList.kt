package xyz.world.currency.rate.converter.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.currencies_item.view.*
import kotlinx.android.synthetic.main.currency_list.*
import kotlinx.android.synthetic.main.currency_list_view.*
import kotlinx.android.synthetic.main.entry_configurations.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseDataModel
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.R
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.data.download.currencies.UpdateSupportedListData
import xyz.world.currency.rate.converter.data.download.rates.UpdateCurrenciesRatesData
import xyz.world.currency.rate.converter.ui.adapter.CurrencyAdapter
import xyz.world.currency.rate.converter.ui.adapter.CustomLinearLayoutManager
import xyz.world.currency.rate.converter.utils.checkpoints.NetworkConnectionListener
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints
import xyz.world.currency.rate.converter.utils.saved.CountryData
import xyz.world.currency.rate.converter.utils.ui.setupUI

class CurrencyList : Fragment(), View.OnClickListener, View.OnLongClickListener {

    var currencyAdapter: CurrencyAdapter? = null

    var currenciesListData: ArrayList<DatabaseDataModel> = ArrayList<DatabaseDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.currency_list_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(activity!!)

        val customLinearLayoutManager = CustomLinearLayoutManager(context, RecyclerView.VERTICAL, false)
        loadView.layoutManager = customLinearLayoutManager

        val systemCheckpoints = SystemCheckpoints(context!!)

        val currencyDataViewModel: CurrencyDataViewModel = ViewModelProviders
            .of(this@CurrencyList)
            .get(CurrencyDataViewModel::class.java)

        currencyDataViewModel.supportedCurrencyList.observe(viewLifecycleOwner,
            Observer<List<DatabaseDataModel>>  {
                currenciesListData = it as ArrayList<DatabaseDataModel>

                if (currenciesListData.isNotEmpty()) {
                    setupListOfSupportedCurrencies(currenciesListData)
                }
            })

        currencyDataViewModel.recyclerViewItemsRatesExchange.observe(this@CurrencyList,
            Observer<ArrayList<RecyclerViewItemsDataStructure>> {
                if (currencyAdapter == null) {
                    if (it.size > 0) {
                        progressBar.visibility = View.GONE
                        activity!!.toolbarOption.setImageDrawable(context?.getDrawable(R.drawable.refresh_icon))
                    } else {
                        activity!!.toolbarOption.setImageDrawable(context?.getDrawable(R.drawable.no_internet))
                    }

                    currencyAdapter = CurrencyAdapter(context!!)
                    currencyAdapter?.recyclerViewItemsDataStructure = it

                    loadView.adapter = currencyAdapter
                    currencyAdapter!!.notifyDataSetChanged()

                    println(">>> 11")

                } else {

                    println(">>> 22")

                    currencyAdapter?.recyclerViewItemsDataStructure = it
                    currencyAdapter?.notifyItemRangeChanged(0, currencyAdapter!!.itemCount, it)
                }

                println(">>> 33")

                Log.d("LiveData", "Observing ItemsDataStructure")
            })

        CurrencyDataViewModel.baseCurrency.observe(viewLifecycleOwner,
            Observer { newBaseCurrency ->

                UpdateCurrenciesRatesData(systemCheckpoints)
                    .triggerCloudDataUpdateForce(context!!, currencyDataViewModel)

                Snackbar.make(mainView, "${context!!.getString(R.string.selectedCurrency)} ${newBaseCurrency}", Snackbar.LENGTH_LONG).show()

                Glide.with(context!!)
                    .load(CountryData().flagCountryLink(PreferencesHandler(context!!).CurrencyPreferences().readSaveCurrency().toLowerCase()))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            e?.printStackTrace()

                            activity?.runOnUiThread {
                                currentCurrencyFlag.setImageDrawable(context!!.getDrawable(R.drawable.currency_symbols_icon))
                            }

                            return true
                        }

                        override fun onResourceReady(drawable: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            activity?.runOnUiThread {
                                currentCurrencyFlag.setImageDrawable(drawable)
                            }

                            return true
                        }
                    })
                    .submit()

                Log.d("Base Currency Changed", newBaseCurrency)
            })

        activity?.toolbarOption!!.setOnClickListener {
            if (systemCheckpoints.networkConnection()) {
                UpdateCurrenciesRatesData(systemCheckpoints)
                    .triggerCloudDataUpdateForce(context!!, currencyDataViewModel)

                Snackbar.make(mainView, context!!.getString(R.string.updatingForce), Snackbar.LENGTH_LONG).show()
            } else {
                toolbarOption.setImageDrawable(context!!.getDrawable(R.drawable.no_internet))
            }
        }

        UpdateCurrenciesRatesData(systemCheckpoints)
            .triggerCloudDataUpdateSchedule(context!!, currencyDataViewModel)

        UpdateSupportedListData(systemCheckpoints)
            .triggerSupportedCurrenciesUpdate(context!!, currencyDataViewModel)
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            NetworkConnectionListener(it)
        }

        typeRate.setOnEditorActionListener { textView, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (!textView.text.isNullOrEmpty()) {
                        textInputLayout.isErrorEnabled = false

                        val multiplier = textView?.text.toString().toDouble()

                        currencyAdapter?.multiplyNumber = multiplier
                        currencyAdapter?.notifyItemRangeChanged(0, currencyAdapter!!.itemCount, multiplier)
                    } else {
                        if (textView.text.toString() == "0") {
                            textInputLayout.isErrorEnabled = false

                            val multiplier = textView?.text.toString().toDouble()

                            currencyAdapter?.multiplyNumber = multiplier
                            currencyAdapter?.notifyItemRangeChanged(0, currencyAdapter!!.itemCount, multiplier)
                        } else {
                            textInputLayout.isErrorEnabled = true

                            textInputLayout.error = getString(R.string.inputError)
                            textInputLayout.errorIconDrawable = context!!.getDrawable(android.R.drawable.stat_notify_error)
                        }
                    }
                }
            }

            false
        }

        typeRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(afterChangedText: Editable?) {
                if (afterChangedText.isNullOrEmpty()) {
                    //Preventing invalid input
                    textInputLayout.isErrorEnabled = true

                    textInputLayout.error = getString(R.string.inputError)
                    textInputLayout.errorIconDrawable = context!!.getDrawable(android.R.drawable.stat_notify_error)
                } else {
                    if (typeRate.text.toString() == "0") {
                        //Preventing invalid input
                        textInputLayout.isErrorEnabled = true

                        textInputLayout.error = getString(R.string.inputError)
                        textInputLayout.errorIconDrawable = context!!.getDrawable(android.R.drawable.stat_notify_error)
                    } else {
                        textInputLayout.isErrorEnabled = false

                        val multiplier = typeRate.text.toString().toDouble()

                        currencyAdapter?.multiplyNumber = multiplier
                        currencyAdapter?.notifyItemRangeChanged(0, currencyAdapter!!.itemCount, multiplier)
                    }
                }

            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    override fun onClick(clickedView: View?) {

        if (clickedView is ConstraintLayout) {
            UpdateCurrenciesRatesData.CONTINUE_UPDATE_SUBSCRIPTION = false

            PreferencesHandler(context!!).CurrencyPreferences().saveLastCurrency(currenciesListData[clickedView.id].CurrencyCode)
            CurrencyDataViewModel.baseCurrency.postValue(currenciesListData[clickedView.id].CurrencyCode)

            currencyAdapter?.multiplyNumber = 1.0

            //Change [CONTINUE_UPDATE_SUBSCRIPTION] to allow flow continue.
            Handler().postDelayed({
                UpdateCurrenciesRatesData.CONTINUE_UPDATE_SUBSCRIPTION = true
            }, 1500)

            Glide.with(context!!)
                .load(CountryData().flagCountryLink(PreferencesHandler(context!!).CurrencyPreferences().readSaveCurrency().toLowerCase()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        e?.printStackTrace()

                        activity?.runOnUiThread {
                            currentCurrencyFlag.setImageDrawable(context!!.getDrawable(R.drawable.currency_symbols_icon))
                        }

                        return true
                    }

                    override fun onResourceReady(drawable: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        activity?.runOnUiThread {
                            currentCurrencyFlag.setImageDrawable(drawable)
                        }

                        return true
                    }
                })
                .submit()

            currenciesListInclude.visibility = View.GONE
        }
    }

    override fun onLongClick(clieckedView: View?): Boolean {
        if (clieckedView is ConstraintLayout) {
            val toast = Toast.makeText(context,
                currenciesListData[clieckedView.id].CountryName,
                Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        }

        return true
    }

    private fun setupListOfSupportedCurrencies(currenciesListData: ArrayList<DatabaseDataModel>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        currenciesListData.forEachIndexed { index, databaseDataModel ->
            val currenciesItem = layoutInflater.inflate(R.layout.currencies_item, null) as ConstraintLayout
            val flagImageView = currenciesItem.currencyFlagView
            val codeTextView = currenciesItem.currencyCodeView

            codeTextView.text = databaseDataModel.CurrencyCode
            Glide.with(context!!)
                .load(CountryData().flagCountryLink(databaseDataModel.CurrencyCode.toLowerCase()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(flagImageView)

            currenciesItem.id = index
            currenciesItem.setOnClickListener(this@CurrencyList)
            currenciesItem.setOnLongClickListener(this@CurrencyList)
            currenciesListView.addView(currenciesItem)
        }

        currentCurrencyFlag.setOnClickListener { view ->
            currenciesListInclude.visibility = View.VISIBLE
        }

        Glide.with(context!!)
            .load(CountryData().flagCountryLink(PreferencesHandler(context!!).CurrencyPreferences().readSaveCurrency().toLowerCase()))
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()

                    activity?.runOnUiThread {
                        currentCurrencyFlag.setImageDrawable(context!!.getDrawable(R.drawable.currency_symbols_icon))
                    }

                    return true
                }

                override fun onResourceReady(drawable: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    activity?.runOnUiThread {
                        currentCurrencyFlag.setImageDrawable(drawable)
                    }

                    return true
                }
            })
            .submit()
    }

}