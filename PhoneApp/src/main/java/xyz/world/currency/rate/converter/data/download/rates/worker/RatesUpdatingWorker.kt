package xyz.world.currency.rate.converter.data.download.rates.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints

class RatesUpdatingWorker(var context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

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