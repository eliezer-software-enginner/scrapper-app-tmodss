package my.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.work.*;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("BootReceiver", "Sistema reiniciado — reagendando scraping.");

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    ScrappingWorker.class,
                    4, TimeUnit.HOURS
            ).setConstraints(constraints).build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "DailyScrappingJob",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
            );
        }
    }
}
