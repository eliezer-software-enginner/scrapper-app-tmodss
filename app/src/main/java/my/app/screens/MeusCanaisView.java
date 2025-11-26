package my.app.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import my.app.R;
import my.app.activities.MainActivity;
import my.app.data.Channel;
import my.app.storages.ChannelsStorage;

import java.util.concurrent.ExecutorService;

public class MeusCanaisView {

    private final Context ctx;
    private final View root;
    private final ExecutorService executor;

    // Construtor modificado para aceitar o ExecutorService
    public MeusCanaisView(Context ctx, ExecutorService executor) {
        this.ctx = ctx;
        this.executor = executor;
        // Infla o layout principal
        this.root = LayoutInflater.from(ctx).inflate(R.layout.view_meus_canais, null);

        Button btnAdd = root.findViewById(R.id.btnAdicionarCanal);

        // Chama carregarCanais com o container correto
        LinearLayout container = root.findViewById(R.id.containerCanais);
        carregarCanais(container); // Primeira carga

        btnAdd.setOnClickListener(v -> adicionarNovoCanal(container));
    }

    /**
     * Carrega e renderiza todos os canais salvos, limpando o container primeiro.
     */
    private void carregarCanais(LinearLayout container) {
        // Limpa o container antes de recarregar para evitar duplicatas
        container.removeAllViews();

        // Acessa o Storage e carrega os canais
        var storage = new ChannelsStorage(ctx);

        for (Channel c : storage.getChannels()) {
            adicionarItemCanal(container, c);
        }
    }

    private void adicionarNovoCanal(LinearLayout container) {
        Channel novo = new Channel("", "");
        adicionarItemCanal(container, novo);
    }

    private void adicionarItemCanal(LinearLayout container, Channel canal) {

        View item = LayoutInflater.from(ctx).inflate(R.layout.item_canal, null);

        EditText inputNome = item.findViewById(R.id.editNomeCanal);
        EditText inputUrl = item.findViewById(R.id.editCanalId);
        Button btnSalvar = item.findViewById(R.id.btnSalvarCanal);
        Button btnRemover = item.findViewById(R.id.btnRemoverCanal); // NOVO: Botão de remover

        // Preenche dados atuais
        inputNome.setText(canal.nome == null || canal.nome.isEmpty() ? "Nome do canal" : canal.nome);
        inputUrl.setText(canal.chatId);

        // Listener para Salvar
        btnSalvar.setOnClickListener(v -> {
            canal.nome = inputNome.getText().toString().trim();
            canal.chatId = inputUrl.getText().toString().trim();

            if (canal.chatId.isEmpty()) {
                Toast.makeText(ctx, "O Chat ID não pode ser vazio.", Toast.LENGTH_SHORT).show();
                return;
            }

            // EXECUTA O SALVAMENTO EM THREAD DE BACKGROUND
            executor.execute(() -> {
                new ChannelsStorage(ctx).addOrUpdateChannel(canal);

                // Volta para a UI thread para mostrar o Toast e RECARREGAR A LISTA
                ((MainActivity) ctx).runOnUiThread(() -> {
                    Toast.makeText(ctx, "Canal salvo", Toast.LENGTH_SHORT).show();
                    carregarCanais(container); // Recarrega a lista para refletir a atualização
                });
            });
        });

        // Listener para Remover
        btnRemover.setOnClickListener(v -> {
            // Se o canal não tem um chatId (é um item novo não salvo), apenas remove visualmente.
            if (canal.chatId == null || canal.chatId.isEmpty()) {
                container.removeView(item);
                Toast.makeText(ctx, "Item não salvo removido", Toast.LENGTH_SHORT).show();
                return;
            }

            // EXECUTA A REMOÇÃO EM THREAD DE BACKGROUND
            executor.execute(() -> {
                new ChannelsStorage(ctx).removeChannel(canal);

                // Volta para a UI thread para mostrar o Toast e RECARREGAR A LISTA
                ((MainActivity) ctx).runOnUiThread(() -> {
                    Toast.makeText(ctx, "Canal removido", Toast.LENGTH_SHORT).show();
                    carregarCanais(container); // Recarrega a lista para refletir a remoção
                });
            });
        });

        container.addView(item);
    }


    public View getView() {
        return root;
    }
}