package my.app;

import android.os.Environment;
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
    // Nota: O token '8214368967:AAFN-Hq8bNU1pue0o4ysK_FsxQ5jde8mTXs' está visível.
    // Em um projeto real, ele deve ser armazenado de forma segura (e.g., secrets ou build config).
    private final TelegramClient telegramClient = new OkHttpTelegramClient(Env.BOTTOKEN);
    private final UrlShortener urlShortener = new UrlShortener();

    @Override
    public void consume(Update update) {
        // Lógica de consumo de updates (chat, etc.) permanece inalterada
    }

    public void sendMessageTo(Content content, String chat_id){

        var url = urlShortener.shortUrl(content.link);

        InlineKeyboardMarkup keyboard = getDownloadInlineButton(url);

        // 4. Monta a mensagem SendPhoto
        var msg = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile(content.imgUrl))
                .caption(createCaption(content, url))
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