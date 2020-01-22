package xyz.world.currency.rate.converter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.currency_list_view.*
import kotlinx.android.synthetic.main.entry_configurations.*
import xyz.world.currency.rate.converter.R
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.ui.adapter.CurrencyAdapter
import xyz.world.currency.rate.converter.ui.adapter.CustomLinearLayoutManager
import xyz.world.currency.rate.converter.utils.checkpoints.NetworkConnectionListener
import xyz.world.currency.rate.converter.utils.ui.setupUI


class CurrencyList : Fragment() {

    var currencyAdapter: CurrencyAdapter? = null

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

        val currencyDataViewModel: CurrencyDataViewModel = ViewModelProviders
            .of(this@CurrencyList)
            .get(CurrencyDataViewModel::class.java)

        currencyDataViewModel.recyclerViewItemsCurrencyData.observe(viewLifecycleOwner,
            Observer<ArrayList<RecyclerViewItemsDataStructure>> {
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

                Log.d("LiveData", "Observing ItemsDataStructure")
            })

        currencyDataViewModel.recyclerViewItemsCurrencyRate.observe(viewLifecycleOwner,
            Observer<ArrayList<RecyclerViewItemsDataStructure>> { payloadData ->
                currencyAdapter?.recyclerViewItemsDataStructurePayload = payloadData
                currencyAdapter?.notifyItemRangeChanged(0, currencyAdapter!!.itemCount, payloadData)

                Log.d("LiveData", "Observing ItemsCurrencyRate")
            })

        currencyDataViewModel.baseCurrency.observe(viewLifecycleOwner,
            Observer { newBaseCurrency ->


                Log.d("Base Currency Changed", newBaseCurrency)
            })
    }

    override fun onStart() {
        super.onStart()

        activity?.let {
            NetworkConnectionListener(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}