package my.app;

import android.content.Context;
import my.app.data.Content;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static my.app.UiBuilder.createCaption;
import static my.app.UiBuilder.getDownloadInlineButton;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient = new OkHttpTelegramClient(Env.BOTTOKEN);
    public Context context;

    @Override
    public void consume(Update update) {
        // Lógica de consumo de updates (chat, etc.) permanece inalterada
    }

    public void sendMessageTo(Content content, String chat_id){
        InlineKeyboardMarkup keyboard = getDownloadInlineButton(content.link);

        // 4. Monta a mensagem SendPhoto
        var msg = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile(content.imgUrl))
                .caption(createCaption(content))
                .parseMode("HTML") // **MUITO IMPORTANTE**: Informa ao Telegram para interpretar o 'caption' como HTML (para o negrito)
                .replyMarkup(keyboard) // Anexa o teclado inline à mensagem
                .build();
        try {
            // Executa o envio
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}