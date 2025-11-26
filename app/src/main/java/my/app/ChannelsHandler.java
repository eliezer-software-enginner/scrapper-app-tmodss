package my.app;

import android.content.Context;
import my.app.data.Channel;
import my.app.storages.ChannelsStorage;

import java.util.List;

public class ChannelsHandler {
    public List<Channel> loadChannels(Context ctx) {
        return new ChannelsStorage(ctx).getChannels();
//        return List.of(
//                new Channel("Teste","-1003457993247"),
//                new Channel("Apk livre","-1003406204965"),
//                new Channel("G1 - Noticias Oficiais do g1", "-1002403342784")
//                );
    }
    //salvar canais
    //carregar canais
}
