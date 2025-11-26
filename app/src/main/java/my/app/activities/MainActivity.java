package my.app.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.*;
import com.google.android.material.navigation.NavigationView;
import my.app.R;
import my.app.ScrappingWorker;
import my.app.screens.HomeView;
import my.app.screens.MeusCanaisView;
import my.app.storages.ChannelsStorage; // Mantido para referência, embora não usemos métodos estáticos

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static boolean IsTestMode = false;
    DrawerLayout drawer;
    NavigationView nav;

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    public static final String TAG = "MainActivity";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // REMOVIDO: SAF_REQUEST_CODE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agendarScrappingDiario();
        // REMOVIDO: Chamada a solicitarPermissaoDiretorio() e lógica de checagem de URI.

        setContentView(R.layout.activity_main);

        // 1. Configura a Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Referencia o Drawer e a NavigationView
        drawer = findViewById(R.id.drawerLayout);
        nav = findViewById(R.id.navView);

        // 3. Cria e configura o ActionBarDrawerToggle (ícone de hambúrguer)
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawer.addDrawerListener(toggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Abre a Home no início (sem condições)
        showHome();

        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) showHome();
            // A navegação agora é direta, sem checagem de URI ou permissão.
            if (id == R.id.menu_canais) showMeusCanais();

            drawer.closeDrawers();
            return true;
        });
    }

    // REMOVIDO: solicitarPermissaoDiretorio()
    // REMOVIDO: onActivityResult()

    /**
     * Ponto de sincronização mais robusto para o ActionBarDrawerToggle.
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincroniza o estado do ícone (hamburger ou seta) após o layout ser restaurado.
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Se o clique for no ícone do drawer, o toggle deve lidar com ele.
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Métodos de navegação (inalterados)
    private void showHome() {
        // Simulação
        HomeView view = new HomeView(this);
        findViewById(R.id.container).post(() -> {
            ((android.widget.FrameLayout)findViewById(R.id.container))
                    .removeAllViews();
            ((android.widget.FrameLayout)findViewById(R.id.container))
                    .addView(view.getView());
        });
    }

    private void showMeusCanais() {
        // Simulação
        MeusCanaisView view = new MeusCanaisView(this, executor);
        ((android.widget.FrameLayout)findViewById(R.id.container))
                .removeAllViews();
        ((android.widget.FrameLayout)findViewById(R.id.container))
                .addView(view.getView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdownNow();
        }
    }


    void agendarScrappingDiario() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest scrappingRequest =
                new PeriodicWorkRequest.Builder(ScrappingWorker.class,
                        4, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyScrappingJob",
                ExistingPeriodicWorkPolicy.KEEP,
                scrappingRequest);

        Log.i(TAG, "Agendamento de Scrapping Diário configurado com sucesso.");
    }
}