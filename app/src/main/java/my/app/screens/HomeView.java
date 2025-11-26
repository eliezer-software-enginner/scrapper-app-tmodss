package my.app.screens;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import my.app.*;
import my.app.activities.MainActivity;
import my.app.data.Content;
import my.app.storages.Storage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeView {

    private final Context ctx;
    private final View root;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public HomeView(Context ctx) {
        this.ctx = ctx;
        this.root = LayoutInflater.from(ctx).inflate(R.layout.home_view, null);

        TextView txtStatus = root.findViewById(R.id.txtStatus);
        TextView txtTest = root.findViewById(R.id.txtTestMode);
        Button btnScrap = root.findViewById(R.id.btnScrap);
        LinearLayout container = root.findViewById(R.id.containerPosts);

        txtTest.setText(MainActivity.IsTestMode ? "TestMode:true" : "TestMode:false");

        btnScrap.setOnClickListener(v -> executor.execute(() -> {
            try {
                new SenderContentService(ctx).send();
                ((Activity) ctx).runOnUiThread(() -> txtStatus.setText("Atualizado!"));
            } catch (IOException e) {
                Log.e("HomeView", "Erro", e);
            }
        }));

        loadPosts(container);
    }

    private void loadPosts(LinearLayout container) {
        Storage storage = new Storage(ctx);
        Map<String, Content> posts = storage.postCache;

        for (String key : posts.keySet()) {
            Content p = posts.get(key);

            View item = LayoutInflater.from(ctx).inflate(R.layout.item_post, null);

            TextView title = item.findViewById(R.id.postTitle);
            ImageView img = item.findViewById(R.id.postImg);
            EditText input = item.findViewById(R.id.inputImgUrl);
            Button btnAtualizar = item.findViewById(R.id.btnAtualizar);

            title.setText(p.title);

            if (p.imgUrl != null && !p.imgUrl.isEmpty()) {
                executor.execute(() -> {
                    try {
                        var bmp = android.graphics.BitmapFactory.decodeStream(
                                new java.net.URL(p.imgUrl).openStream()
                        );

                        ((Activity) ctx).runOnUiThread(() -> img.setImageBitmap(bmp));

                    } catch (Exception e) {
                        Log.e("HomeView", "Erro carregando imagem", e);
                    }
                });
            }

            btnAtualizar.setOnClickListener(v -> {
                String url = input.getText().toString().trim();

                executor.execute(() -> {
                    new Storage(ctx).updateImageUrl(p.title, url);
                    ((Activity) ctx).runOnUiThread(() ->
                            Toast.makeText(ctx, "Atualizado!", Toast.LENGTH_SHORT).show());
                });
            });

            container.addView(item);
        }
    }

    public View getView() { return root; }
}
