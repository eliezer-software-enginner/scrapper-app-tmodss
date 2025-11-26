package my.app;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.IOException;

public class ScrappingWorker extends Worker {
    private static final String TAG = "ScrappingWorker";

    public ScrappingWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Iniciando tarefa de Scrapping diário...");

        try {
            // **AQUI: Sua lógica de rede/scrapping deve ser executada**
            var sender = new SenderContentService(this.getApplicationContext());
            sender.send(); // Execução da tarefa de scraping

            Log.i(TAG, "Scrapping concluído com sucesso.");

            // Retorna sucesso para indicar que o trabalho foi concluído com êxito
            return Result.success();

        } catch (IOException e) {
            Log.e(TAG, "Erro durante a execução do Scrapping.", e);

            // Dependendo do seu caso:
            // - Result.retry(): para tentar novamente (útil para erros temporários de rede).
            // - Result.failure(): para não tentar novamente.
            return Result.failure();
        }
    }
}