package my.app;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.*;
import androidx.work.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity"; // Para logs

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

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1
            );
        }


        agendarScrappingDiario();

        ScrollView scroll = new ScrollView(this);

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

        scroll.addView(layout);
        setContentView(scroll);

        btn.setOnClickListener(ev->handleClick());

        // --- INÍCIO DA SOLUÇÃO ---

        Storage storage = new Storage(this);

        LinearLayout listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listContainer);

        Map<String, Content> posts = new Storage(this).postCache;

        for (String key : posts.keySet()) {
            Content p = posts.get(key);

            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);

            TextView titleView = new TextView(this);
            assert p != null;
            titleView.setText(p.title);

            LinearLayout container = new LinearLayout(this);container.setGravity(Gravity.CENTER_HORIZONTAL);

            ImageView img = new ImageView(this);

            Runnable showNoImg = () -> {
                TextView n = new TextView(this);
                n.setText("Sem imagem");
                container.addView(n);
            };

            if (p.imgUrl != null && !p.imgUrl.isEmpty()) {
                executor.execute(() -> {
                    try {
                        var is = new java.net.URL(p.imgUrl).openStream();
                        var bmp = android.graphics.BitmapFactory.decodeStream(is);
                        runOnUiThread(() -> {
                            img.setImageBitmap(bmp);
                            container.addView(img);
                        });
                    } catch (Exception e) {
                        Log.i(MainActivity.TAG, "carregamento: " + e.getMessage());
                        runOnUiThread(showNoImg);
                    }
                });
            } else showNoImg.run();

            EditText input = new EditText(this);
            input.setHint("Nova URL da imagem");

            Button btnUpdate = new Button(this);
            btnUpdate.setText("Atualizar");
            btnUpdate.setOnClickListener(v -> {
                String newUrl = input.getText().toString().trim();
                executor.execute(() -> {
                    storage.updateImageUrl(p.title, newUrl);

                    try{
                        var is = new java.net.URL(newUrl).openStream();
                        var bmp = android.graphics.BitmapFactory.decodeStream(is);

                        runOnUiThread(() -> {
                            container.removeAllViews();  // remove "Sem imagem" OU imagem antiga
                            img.setImageBitmap(bmp);
                            container.addView(img);
                            Toast.makeText(this, "Imagem atualizada", Toast.LENGTH_LONG).show();
                        });


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            box.addView(titleView);
            box.addView(container);
            box.addView(input);
            box.addView(btnUpdate);

            listContainer.addView(box);

        }

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
                var sender = new SenderContentService(this);
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