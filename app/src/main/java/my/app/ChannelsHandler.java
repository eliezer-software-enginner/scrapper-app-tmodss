package my.app;

import java.util.List;

public class ChannelsHandler {
    public record Channel(String nome, String chatdId){}
    public List<Channel> loadChannels() {
        return List.of(
                new Channel("Teste","-1003457993247"),
                new Channel("Apk livre","-1003406204965"),
                new Channel("G1 - Noticias Oficiais do g1", "-1002403342784")
                );
    }
    //salvar canais
    //carregar canais
}
