package my.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScrappingPage {
    final static String  URL = "https://tekmods.com/";

    public List<Content> fetchList() throws IOException {
        //ir em div que tem o id "content" -> depois entrar em main que tem o id "primary"
        //e então entra na div que possui a classe "row"

        //na div que possui a classe "row" traz uma lista divs cada uma com id "listpost"
        //que possui <a/> com href que vamos pegar e também possui title que vamos pegar também.
        //dentro de <a/> possui uma div com uma <img/> essa <img/< possui src que também vamos pegar.


        Document doc = Jsoup.connect(URL).get();
        var list = new ArrayList<Content>();

        // 1. div.content
        Element content = doc.selectFirst("div#content");
        if (content == null) return Collections.emptyList();

        // 2. main#primary
        Element primary = content.selectFirst("main#primary");
        if (primary == null) return Collections.emptyList();

        // 3. div.row
        Element row = primary.selectFirst("div.row");
        if (row == null) return Collections.emptyList();

        // 4. todas divs com id=listpost
        Elements posts = row.select("div#listpost");

        for (Element post : posts) {

            Element a = post.selectFirst("a");
            if (a == null) continue;

            String link = a.attr("href");
            String title = a.attr("title");

// 6. pegar a <img> dentro do <a><div>
            Element firstDiv = a.selectFirst("div");
            if(firstDiv == null)continue;

            Element img = firstDiv.selectFirst("img");
            String imageSrc = (img != null) ? img.attr("src") : null;
            if (imageSrc != null && imageSrc.startsWith("data:image")) {
                imageSrc = null; // ou continue;
            }

// 7. segunda div possui outra div e tres <spans/>
// Quero pegar o conteúdo desses spans
// Elements secondDiv = a.select("div")[1]; // <--- CORREÇÃO AQUI
            Elements allDivs = a.select("div");
            if (allDivs.size() < 2) continue; // Garante que há uma segunda div

            Element secondDiv = allDivs.get(1); // Pega o segundo elemento <div> dentro do <a>

// Pega todos os spans diretamente dentro desta segunda div
            Elements spans = secondDiv.select("span.align-middle, span.text-truncate");

            String version = !spans.isEmpty() ? spans.get(0).text() : "";
            String plus     = spans.size() > 1 ? spans.get(1).text() : "";
            String info     = spans.size() > 2 ? spans.get(2).text() : "";

// imprimir resultados
            System.out.println("Link: " + link);
            System.out.println("Title: " + title);
            System.out.println("Image: " + imageSrc);
            System.out.println("Span 1: " + version);
            System.out.println("Span 2: " + plus);
            System.out.println("Span 3: " + info);
            System.out.println("--------------------------------");

            var obj = new Content();
            obj.title = title;
            obj.imgUrl = imageSrc;
            obj.link = link;
            obj.info = info;
            obj.version = version;

            list.add(obj);
        }
        // exemplo do título
        System.out.println(doc.title());

        return list;
    }
}


