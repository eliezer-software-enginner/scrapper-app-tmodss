package my.app;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class UiBuilder {

    public static InlineKeyboardMarkup getDownloadInlineButton(String url) {
        // 2. Cria o botão inline "Download"
        // IMPORTANTE: Substitua "https://your.download.link" pela URL real do seu conteúdo.
        InlineKeyboardButton downloadButton = InlineKeyboardButton.builder()
                .text("⬇️ Download")
                .url(url) // URL para o download
                .build();

        var keyboardRow = new InlineKeyboardRow(
                List.of(downloadButton)
        );

        // 3. Cria o layout do teclado: uma linha com o botão
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();
        return keyboard;
    }

    public static String createCaption(Content content, String url){
        // Usamos StringBuilder para construir a string de forma eficiente
        StringBuilder sb = new StringBuilder();

        // 1. Título principal (RAR) - Negrito
        // \uD83D\uDCF6 é o emoji de "Signal" / "Antena"
        sb.append("<b>\uD83D\uDCF6 ").append(content.title).append("</b>").append("\n\n");

        // 2. Versão, Tamanho, Rating - Negrito
        // vs v7.20.build128 💾 6 MB ⭐ 4.3
        sb.append("\uD83C\uDD9A <b>").append(content.version).append("</b> \uD83D\uDCBE <b>")
        //        .append(content.size()).append("</b> \u2B50\uFE0F <b>")
             .append(content.getRatingDinamico()).append("</b>").append("\n");

        // 3. Como Usar & PlayStore - Links
        // ℹ️ Como Usar 🔶 PlayStore app
        // Assumindo content.howToUseLink() e content.playStoreLink()
        sb.append("ℹ️ <a href=\"").append(url).append("\">Como Usar</a> \uD83D\uDD36 <a href=\"").append(url).append("\">PlayStore app</a>").append("\n\n");

        // 4. Separador
        sb.append("------------------------------------------").append("\n\n");

        // 5. Info (Premium Desbloqueado) - Itálico
        // 💎 Info: *Premium Desbloqueado*
        sb.append("\uD83D\uDC8E Info: <i>").append(content.info).append("</i>").append("\n");

        // 7. Separador
        sb.append("------------------------------------------").append("\n\n");

        // 8. DOWNLOAD (Link para o próprio content.link()) - Negrito + Link
        // 🔗 DOWNLOAD
        sb.append("\uD83D\uDD17 <b><a href=\"").append(url).append("\">DOWNLOAD</a></b>").append("\n\n");

        // 9. Separador
        sb.append("------------------------------------------").append("\n\n");

        // 11. Canais/Créditos - Negrito
        // © @Tekmods | @CarecaApk | @CentralTek
        //  sb.append("\u00A9 <b>").append(content.channels()).append("</b>").append("\n\n");

        // 12. Coração/Likes (Valor estático conforme o print) - Negrito
        // ❤️ 1
       // sb.append("\u2764\uFE0F <b>1</b>");

        return sb.toString();
    }
}
