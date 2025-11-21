package my.app;

import java.io.IOException;

public class SenderContentService {
    //load saved channels
    //send content to them (use addly) for links

    ChannelsHandler channelsHandler = new ChannelsHandler();
    Bot bot = new Bot();
    ScrappingPage scrappingPage = new ScrappingPage();

    public void send() throws IOException {
        var channels = channelsHandler.loadChannels();
        var list = scrappingPage.fetchList();
      //  var content = scrappingPage.fetchList().get(0);

        for (ChannelsHandler.Channel canal : channels) {
            list.forEach(conteudo ->{
                bot.sendMessageTo(conteudo, canal.chatdId());
            });

           //     bot.sendMessageTo(content, canal.chatdId());

        }
    }
}
