package xyz.world.currency.rate.converter.data.download.rates.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints

class RatesUpdatingWorker(var context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    /*
     * UpdateCurrenciesRatesDataWorkManager().triggerCloudDataUpdateScheduleWorkManager(context!!)
     *
     * To have Even Better Friendly App with CPU & Battery, I didn't use fixed scheduler to download & update database from servers.
     * So, I added an Observer to repeat every 30 minutes until the app is running.
     * & to avoid recalling for fetching data from server every time user open the app, I am saving time when Observable Subscription occur...
     * then compare saved time with next time to check if it is been 30 minutes since last API Call.
     */
    override suspend fun doWork(): Result {

        return try {

            UpdateCurrenciesRatesDataWorkManager()
                .jsonObjectRequestWorkManager(context, SystemCheckpoints(context))


            Result.success()
        } catch (e: Exception) {

            Result.failure()
        }
    }
}