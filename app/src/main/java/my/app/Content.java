package my.app;

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
}