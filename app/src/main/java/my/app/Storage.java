package my.app;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Storage {

    private static final String FILENAME = "posts.json";
    private final File storageFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Content> postCache = new HashMap<>();

    public Storage(Context ctx) {

        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File folder = new File(downloads, "posts-tekmods");

        if (!folder.exists()) folder.mkdirs();

        this.storageFile = new File(folder, FILENAME);
        load();
    }

    private void load() {
        try {
            if (!storageFile.exists()) {
                Log.i("Storage", "Arquivo não existe, iniciando cache vazio");
                postCache = new HashMap<>();
                return;
            }

            postCache = mapper.readValue(
                    storageFile,
                    new TypeReference<Map<String, Content>>() {}
            );

        } catch (Exception e) {
            Log.e("Storage", "Erro ao carregar JSON: " + e.getMessage());
            postCache = new HashMap<>();
        }
    }

    private void save() {
        try {
            mapper.writeValue(storageFile, postCache);
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
