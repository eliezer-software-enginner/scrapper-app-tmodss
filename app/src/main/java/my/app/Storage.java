package my.app;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Storage {
    private static final String FILENAME = "posts.json";

    // O caminho completo do arquivo no sistema de arquivos
    private final File storageFile;

    // Cache interno para manter o estado atual dos posts em memória (Otimização)
    public Map<String, Content> postCache;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Storage(Context context) {
        File filesDir = context.getFilesDir();
        this.storageFile = new File(filesDir, FILENAME);

        this.postCache = new HashMap<>();
        // Carrega o cache na inicialização
        loadPostsFromFile();
    }

    /**
     * Carrega o Map de posts do arquivo JSON para o cache interno.
     */
    private void loadPostsFromFile() {
        if (!storageFile.exists() || storageFile.length() == 0) {
            // O arquivo não existe ou está vazio, inicializa com um mapa vazio
            this.postCache = new HashMap<>();
            System.out.println("DEBUG: Arquivo posts.json não encontrado ou vazio. Cache inicializado vazio.");
            return;
        }

        try {
            // Usa TypeReference para informar ao Jackson que o JSON é um Map<String, Content>
            TypeReference<Map<String, Content>> typeRef = new TypeReference<>() {
            };
            this.postCache = objectMapper.readValue(storageFile, typeRef);
            System.out.println("DEBUG: " + postCache.size() + " posts carregados de " + FILENAME);
        } catch (IOException e) {
            System.err.println("Erro ao carregar posts do arquivo: " + e.getMessage());
            this.postCache = new HashMap<>(); // Falha no carregamento, reseta o cache
        }
    }

    /**
     * Salva o Map completo de posts (o cache) de volta no arquivo JSON.
     */
    private void savePostsToFile() {
        try {
            // Escreve o Map completo para o arquivo.
            objectMapper.writeValue(storageFile, postCache);
            System.out.println("DEBUG: Cache de posts salvo com sucesso em " + FILENAME);
        } catch (IOException e) {
            System.err.println("Erro ao salvar posts no arquivo: " + e.getMessage());
        }
    }


    /**
     * Método para salvar um post (em json)
     * Adiciona/Atualiza o post no cache e persiste o cache inteiro no arquivo JSON.
     *
     * @param post O objeto Content a ser salvo.
     */
    public void savePost(Content post) {
        if (post == null || post.title == null || post.title.trim().isEmpty()) {
            System.err.println("Erro ao salvar: O post ou seu título está vazio.");
            return;
        }

        // 1. Atualiza o cache interno, usando o título como chave
        postCache.put(post.title, post);

        // 2. Salva o cache completo no arquivo
        savePostsToFile();
    }

    /**
     * Método para devolver um post pelo nome ou retornar null se não achar.
     *
     * @param title O título (chave) do post a ser recuperado.
     * @return O objeto Content recuperado ou null se não for encontrado.
     */
    public Content getPostByName(String title) {
        if (title == null || title.trim().isEmpty()) {
            System.err.println("Erro ao buscar: O título está vazio.");
            return null;
        }

        // Busca o post diretamente no cache carregado em memória
        Content post = postCache.get(title);

        // System.out.println("DEBUG: Post não encontrado para o título: " + title);
        // Retorna null se não achar

        return post;
    }

    public void updateImageUrl(String title, String newUrl) {
        if (title == null || title.trim().isEmpty()) return;
        if (newUrl == null || newUrl.trim().isEmpty()) return;

        Content post = postCache.get(title);
        if (post == null) return;

        post.imgUrl = newUrl;
        postCache.put(title, post);
        savePostsToFile();
    }

}