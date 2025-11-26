package my.app;

import android.content.Context;
import my.app.data.Channel;

import java.io.IOException;

public class SenderContentService {
    //load saved channels
    //send content to them (use addly) for links
    Context context;
    ChannelsHandler channelsHandler = new ChannelsHandler();
    Bot bot = new Bot();
    ScrappingPage scrappingPage;

    public SenderContentService(Context context){
        this.context = context;
        this.bot.context = context;
        this.scrappingPage = new ScrappingPage(context);
    }

    public void send() throws IOException {
        var channels = channelsHandler.loadChannels(this.context);
        var list = scrappingPage.fetchList();
      //  var content = scrappingPage.fetchList().get(0);

        for (Channel canal : channels) {
            list.forEach(conteudo ->{
                bot.sendMessageTo(conteudo, canal.chatId);
            });

           //     bot.sendMessageTo(content, canal.chatdId());

        }
    }
}
