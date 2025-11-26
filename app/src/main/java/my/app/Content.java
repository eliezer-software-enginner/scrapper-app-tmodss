package my.app;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Content{
    public String title, link,
            imgUrl,
            version,
    //howToUseLink,
    info;
    //channels,
    // playStoreLink;

    public String getRatingDinamico(){
        double numero = 3 + (Math.random() * 2); // entre 3 e 5
        double comUmaCasa = Math.round(numero * 10.0) / 10.0;

        System.out.println(comUmaCasa);
        return String.valueOf(comUmaCasa);
    }

    public String getDtAtualizacaoDinamica(){
        // 1. Obter a data e hora atuais
        LocalDateTime agora = LocalDateTime.now();

        // 2. Definir o Locale para Português do Brasil,
        //    garantindo que o nome do mês seja formatado corretamente.
        Locale localePtBr = new Locale("pt", "BR");

        // 3. Definir o formatador para a parte da data: "dd de MMMM, yyyy"
        //    MMMM representa o nome completo do mês (ex: "novembro").
        DateTimeFormatter formatadorData =  DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", localePtBr);

        // 4. Formatar a parte da data
        String dataFormatada  = agora.format(formatadorData);

        // 5. Definir o formatador para a parte da hora: "HH:mm"
        DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH:mm");

        // 6. Formatar a parte da hora
        String horaFormatada  = agora.format(formatadorHora);

        // 7. Combinar as partes na String final
        // Exemplo: "⏰ Atualizado às 10:45 - 26 de novembro, 2025"

        return "%s  -  %s".formatted(horaFormatada, dataFormatada);
    }
}