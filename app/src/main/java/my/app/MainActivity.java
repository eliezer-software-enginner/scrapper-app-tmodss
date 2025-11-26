package my.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.work.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity"; // Para logs

    LinearLayout layout;
    TextView contentView;

    Button btn;
    TextView textViewIsTestMode;

    public static boolean IsTestMode = false;

    // Crie um executor para rodar tarefas em background
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        agendarScrappingDiario();

        layout = new LinearLayout(this);
        btn = new Button(this);
        contentView = new TextView(this);
        textViewIsTestMode = new TextView(this);
        layout.setOrientation(LinearLayout.VERTICAL);


        contentView.setText("Idle");
        textViewIsTestMode.setText(MainActivity.IsTestMode?"TestMode:True":"TestMode:False");
        layout.addView(contentView);
        layout.addView(btn);
        layout.addView(textViewIsTestMode);

        setContentView(layout);

        btn.setOnClickListener(ev->handleClick());

        // --- INÍCIO DA SOLUÇÃO ---
    }

    // É uma boa prática desligar o executor ao destruir a Activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    void handleClick(){
        // 1. Envie a tarefa de rede para o Executor
        executor.execute(() -> {
            try {
                // 2. A operação de rede (que pode bloquear) é executada aqui, na Thread de background
                var sender = new SenderContentService();
                sender.send();

                // Exemplo de como atualizar a UI após a conclusão (opcional)
                final String result = "Dados Scrapeados com Sucesso!";

                // 3. Volte para a Thread Principal (UI Thread) para atualizar a interface
                runOnUiThread(() -> {
                    contentView.setText(result);
                });

            } catch (IOException e) {
                // Tratar erros, logar ou mostrar um Toast
                Log.e(TAG, "Erro ao realizar operação de rede", e);
                runOnUiThread(() -> {
                    contentView.setText("Erro: " + e.getMessage());
                });
            }
        });
    }

    void agendarScrappingDiario() {
        // 1. Defina as restrições
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Requer conexão de rede
                .build();
        // 1. Criar a requisição de trabalho periódico (Repetindo a cada 24 horas)
        // O WorkManager garante que o intervalo mínimo de repetição seja de 15 minutos,
        // mas é ideal para tarefas diárias.
        PeriodicWorkRequest scrappingRequest =
                new PeriodicWorkRequest.Builder(ScrappingWorker.class,
                        4, TimeUnit.HOURS) // Define o intervalo de repetição como 24 horas
                        .setConstraints(constraints) // Opcional: Adicione restrições como "necessita de rede"
                        .build();

        // 2. Enfileirar a requisição
        // .enqueueUniquePeriodicWork garante que apenas uma instância deste trabalho
        // esteja ativa a qualquer momento, mesmo que o aplicativo seja reiniciado.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyScrappingJob", // Um nome único para sua tarefa
                ExistingPeriodicWorkPolicy.KEEP, // KEEP: Se a tarefa já estiver agendada, mantém a anterior
                scrappingRequest);

        Log.i(TAG, "Agendamento de Scrapping Diário configurado com sucesso.");
    }
}