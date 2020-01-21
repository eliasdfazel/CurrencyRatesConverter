package xyz.world.currency.rate.converter.ui

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.android.synthetic.main.currency_list_view.*
import kotlinx.android.synthetic.main.entry_configurations.*
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.CurrencyDataInterface
import xyz.world.currency.rate.converter.R
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.data.room.DatabasePath
import xyz.world.currency.rate.converter.ui.adapter.CurrencyAdapter
import xyz.world.currency.rate.converter.ui.adapter.CustomLinearLayoutManager
import xyz.world.currency.rate.converter.utils.checkpoints.NetworkConnectionListener
import xyz.world.currency.rate.converter.utils.ui.setupUI


class CurrencyList : Fragment() {

    var currencyAdapter: CurrencyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Thread {


            //BOOOOOOM
    //            val roomDatabase = Room.databaseBuilder(context!!, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
    //                .allowMainThreadQueries()
    //                .build()
    //            AddNewTable().addNewTableForNewBaseCurrency(roomDatabase, "EUR")
    //
    //            val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase
    //
    //            supportSQLiteDatabaseWrite.insert("EUR", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
    //                this.put("CurrencyCode", "USD")
    //                this.put("FullCurrencyName", "America Dollar")
    //                this.put("CurrencyRate", 1.16)
    //                this.put("LastUpdateTime", 123.321)
    //            })
    //
    //            supportSQLiteDatabaseWrite.insert("EUR", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
    //                this.put("CurrencyCode", "CAD")
    //                this.put("FullCurrencyName", "Canadian Dollar")
    //                this.put("CurrencyRate", 1.45)
    //                this.put("LastUpdateTime", 123.321)
    //            })


            val roomDatabaseRead = Room.databaseBuilder(context!!, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
                .allowMainThreadQueries()
                .build()

            val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabaseRead.openHelper.readableDatabase
            val cursor: Cursor = supportSQLiteDatabase.query("SELECT * FROM EUR")
            cursor.moveToFirst()

            while (!cursor.isAfterLast) {

                println(">>>  ${cursor.getString(cursor.getColumnIndex("CurrencyCode"))} - ${cursor.getString(cursor.getColumnIndex("FullCurrencyName"))} " +
                        "- ${cursor.getDouble(cursor.getColumnIndex("CurrencyRate"))}")
                cursor.moveToNext()
            }

            roomDatabaseRead.close()


        }.start()


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