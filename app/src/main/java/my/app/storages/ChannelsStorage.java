package my.app.storages;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.app.data.Channel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia o armazenamento da lista de canais usando o diretório de arquivos interno
 * do aplicativo. Esta abordagem não requer permissões externas.
 */
public class ChannelsStorage {

    private static final String FILENAME = "channels.json";

    // 1. Armazena a referência direta ao arquivo, como na classe Storage.
    private final File storageFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Channel> channels = new ArrayList<>();

    // =================================================================
    // CONSTRUTOR E MÉTODOS DE E/S (I/O)
    // =================================================================

    public ChannelsStorage(Context ctx) {
        // 2. Inicializa o objeto File no construtor
        // Usamos o getFilesDir() do Context, que aponta para o diretório privado do app.
        this.storageFile = new File(ctx.getFilesDir(), FILENAME);
        load();
    }

    /**
     * Carrega a lista de canais do arquivo interno.
     */
    private void load() {
        try {
            // 3. Verifica apenas a existência do arquivo e usa mapper direto.
            if (!storageFile.exists() || storageFile.length() == 0) {
                Log.i("ChannelsStorage", "Arquivo não existe ou está vazio. Iniciando cache vazio.");
                channels = new ArrayList<>();
                // Não chamamos save() aqui, para evitar escrita desnecessária.
                return;
            }

            channels = mapper.readValue(
                    storageFile,
                    new TypeReference<List<Channel>>() {}
            );
            Log.i("ChannelsStorage", "Canais carregados com sucesso: " + channels.size());

        } catch (Exception e) {
            // Erro de parsing (JSON malformado) ou erro de I/O.
            Log.e("ChannelsStorage", "Erro ao carregar JSON do armazenamento interno: " + e.getMessage());
            channels = new ArrayList<>();
        }
    }

    /**
     * Salva a lista de canais no arquivo interno, sobrescrevendo o conteúdo.
     */
    private void save() {
        try {
            // 4. Usa o mapper diretamente no objeto File (como na classe Storage).
            mapper.writeValue(storageFile, channels);
            Log.i("ChannelsStorage", "JSON salvo com sucesso no armazenamento interno.");
        } catch (Exception e) {
            Log.e("ChannelsStorage", "Erro ao salvar JSON no armazenamento interno: " + e.getMessage());
        }
    }

    public List<Channel> getChannels() {
        return channels;
    }
    public void removeChannel(Channel c) {
        channels.removeIf(ch -> c.chatId != null && !c.chatId.isEmpty() && ch.chatId.equals(c.chatId));
        save();
    }
    public void addOrUpdateChannel(Channel c) {
        // Remove canal com mesmo chatId
        channels.removeIf(ch -> ch.chatId.equals(c.chatId));
        channels.add(c);
        save();
    }
}