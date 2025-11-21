package my.app;

import java.util.List;

public class ChannelsHandler {
    public record Channel(String nome, String chatdId){}
    public List<Channel> loadChannels() {
        return List.of(new Channel("Teste","-1003457993247"));
    }
    //salvar canais
    //carregar canais
}
