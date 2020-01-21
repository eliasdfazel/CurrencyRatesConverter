package xyz.world.currency.rate.converter.data.download

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints
import java.util.concurrent.TimeUnit

/**
 * Call for Updating Data on Server Side Or Updating Local Data from Cloud
 */
class UpdateCloudData {

    companion object {
        var CONTINUE_UPDATE_SUBSCRIPTION: Boolean = true
    }

    /**
     * To check current currency & control flow of FlowableItemsDataStructure after base currency changed by item clicks.
     */
    var currentBaseCurrency: String? = null

    /**
     * Invoking Cloud Function to update the currency rates & date in database.
     * It will only pass FirebaseUser.UID as userId to update users specific database and selected base currency to update order of database.
     * All Process of downloading new JsonObject Data & extracting data from JsonObjects will be done on server side.
     * It will only update changed data on database, so on device listener only receive Document Snapshot when data updated.
     */
    fun triggerCloudDataUpdate(context: Context, currencyDataViewModel: CurrencyDataViewModel) {

        Observable
            .interval(30, TimeUnit.MINUTES)
            .repeatUntil {
                currentBaseCurrency != currencyDataViewModel.baseCurrency.value
            }
            .filter {
                Log.d("Base Filter", "$CONTINUE_UPDATE_SUBSCRIPTION")

                SystemCheckpoints(context).networkConnection() && CONTINUE_UPDATE_SUBSCRIPTION
            }
            .doOnNext {
                Log.d("Observable", "${it}")

                //
                //
                //

                //
                //
                //
            }
            .observeOn(Schedulers.io())
            .subscribe()
    }
}

/*WidgetDataModel widgetDataModel = new WidgetDataModel(
                                    System.currentTimeMillis(),
                                    appWidgetId,
                                    InstalledWidgetsAdapter.pickedWidgetPackageName,
                                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider,
                                    InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                    functionsClass.appName(InstalledWidgetsAdapter.pickedWidgetPackageName),
                                    InstalledWidgetsAdapter.pickedWidgetLabel,
                                    false
                            );

                            WidgetDataInterface widgetDataInterface = Room.databaseBuilder(getApplicationContext(), WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                    .fallbackToDestructiveMigration()
                                    .addCallback(new RoomDatabase.Callback() {
                                        @Override
                                        public void onCreate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                            super.onCreate(supportSQLiteDatabase);
                                        }

                                        @Override
                                        public void onOpen(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                            super.onOpen(supportSQLiteDatabase);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
                                                    loadConfiguredWidgets.execute();
                                                }
                                            });
                                        }
                                    })
                                    .build();
                            widgetDataInterface.initDataAccessObject().insertNewWidgetData(widgetDataModel);
                            widgetDataInterface.close();*/