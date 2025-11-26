package my.app.storages;

import android.content.Context;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import my.app.data.Content;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia o armazenamento da lista de posts usando o diretório de arquivos interno
 * do aplicativo.
 */
public class Storage {

    private static final String FILENAME = "posts.json";
    private final File storageFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Content> postCache = new HashMap<>();

    public Storage(Context ctx) {
        // Inicializa o objeto File no construtor, apontando para o diretório
        // de arquivos interno do aplicativo (Context.getFilesDir()).
        this.storageFile = new File(ctx.getFilesDir(), FILENAME);
        load();
    }

    private void load() {
        try {
            // Verifica a existência e o tamanho (evita erro de JSON vazio)
            if (!storageFile.exists() || storageFile.length() == 0) {
                Log.i("Storage", "Arquivo não existe ou está vazio, iniciando cache vazio");
                postCache = new HashMap<>();
                return;
            }

            // Usa o ObjectMapper diretamente com o objeto File.
            postCache = mapper.readValue(
                    storageFile,
                    new TypeReference<Map<String, Content>>() {}
            );
            Log.i("Storage", "Posts carregados com sucesso: " + postCache.size());

        } catch (Exception e) {
            Log.e("Storage", "Erro ao carregar JSON: " + e.getMessage());
            postCache = new HashMap<>();
        }
    }

    private void save() {
        try {
            // Usa o ObjectMapper diretamente com o objeto File.
            mapper.writeValue(storageFile, postCache);
            Log.i("Storage", "JSON de Posts salvo com sucesso no armazenamento interno.");
        } catch (Exception e) {
            Log.e("Storage", "Erro ao salvar JSON: " + e.getMessage());
        }
    }

    public void savePost(Content post) {
        if (post == null || post.title == null || post.title.isEmpty()) return;
        postCache.put(post.title, post);
        save();
    }

    public Content getPostByName(String title) {
        return postCache.get(title);
    }

    public void updateImageUrl(String title, String newUrl) {
        Content post = postCache.get(title);
        if (post == null) return;

        post.imgUrl = newUrl;
        save();
    }
}