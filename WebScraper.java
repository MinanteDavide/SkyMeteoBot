package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.time.Instant;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import org.jsoup.nodes.Element;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WebScraper {

    private String citta;
    private static String url="https://www.3bmeteo.com/meteo/";
    private static Instant now=Instant.now();
    private static String oggi=now.toString().substring(8,10);
    private static String dataCompletaDomani = Instant.now().plus(1, ChronoUnit.DAYS).toString(); //per ottenere la data di domani
    private static String domani=dataCompletaDomani.substring(8,10);


    public static String getCondizioniMeteo(String citta, String data) {

        int tempMedia;
        StringBuilder result = new StringBuilder();
        Instant now=Instant.now();
        String dataCompletaDomani = Instant.now().plus(1, ChronoUnit.DAYS).toString(); //per ottenere la data di domani
        String domani=dataCompletaDomani.substring(8,10);
        try{
            Document document = Jsoup.connect(url + citta).get();
            Elements giornoPrevisione = document.select(".navDays");
            Elements temperatura = document.select(".switchcelsius");
            String statoEffettivo;
            Elements stato = document.select(".navDays img");
            String[] statoMeteo= new String[stato.size()];
            String oggi=now.toString().substring(8,10);
            String[] temp=new String[28];
            String[] dateUtili=new String[giornoPrevisione.size()];


            for (int i = 0; i < giornoPrevisione.size(); i++) {
                String date = giornoPrevisione.get(i).text();
                dateUtili[i]=date.substring(4,6);
                if(i==0) //nel caso il giorno sia il giorno corrente
                    dateUtili[0]=oggi;
                else if(i==1) //nel caso in cui la data sia il giorno seguente nel momento della richiesta della previsione, in quanto 3bmeteo per riferirsi al giorno seguente usa "domani")
                    dateUtili[1]=domani;
                else //tutto il resto dei casi
                    dateUtili[i]=date.substring(4,6);
            }
            for (int i = 0; i < temperatura.size(); i++) {
                String temperaturaP = temperatura.get(i).text();
                if(i<28){ //dopo 28 righe le temperature non sono piu necessarie
                    temp[i]=temperaturaP;
                }
            }

            for (int i = 0; i < stato.size(); i++) {
                Element img = stato.get(i);
                String altText = img.attr("alt");
                statoMeteo[i]=altText;
            }
            String selez=now.toString().substring(0,10);
            String[] dateParts = selez.split("-");
            selez = selez.replace("-", "/");

            selez= dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate date1 = LocalDate.parse(data, formatter1);
            LocalDate date2 = LocalDate.parse(selez, formatter2);
            long giorniDiff = ChronoUnit.DAYS.between(date1, date2);
            if(giorniDiff>300)
                giorniDiff=365-giorniDiff;

            if(giorniDiff<0)
                giorniDiff=-giorniDiff;

            String urlDomani="https://www.3bmeteo.com/meteo/"+citta+"/1";
            Document documentAlt = Jsoup.connect(urlDomani).get();
            Elements alt=documentAlt.select(".navDays img");
            String[] altArr=new String[alt.size()];
            String condOggi="";

            for (int i = 0; i < alt.size(); i++) {
                Element img = alt.get(i);
                String altText = img.attr("alt");
                altArr[i]=altText;
                if(i==0) //il primo elemento è quello della data odierna visto che stiamo usando l'url relativo al giorno successivo
                    condOggi=altText;
            }



            for (int i = 0; i < dateUtili.length; i++) {
                if(data.substring(0,2).equals(dateUtili[i])){
                    if(i>0)
                        result.append(statoMeteo[i-1]);
                }
            }

            if(data.substring(0,2).equals(now.toString().substring(8,10))){
                result.append(condOggi);
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result.toString();

    }



    public static int getMeteoTempMaxGiorno(String citta, String data){
        String tempMax="";
        String urlDomani="https://www.3bmeteo.com/meteo/"+citta+"/1";
        int result;
        try {
            Document document = Jsoup.connect(url + citta).get();
            Document documentAlt = Jsoup.connect(urlDomani).get();
            int[] temp = new int[28];
            Elements giornoPrevisione = document.select(".navDays");
            Elements temperatura = document.select(".switchcelsius");
            Elements tempMaxOggi = documentAlt.select("[title='oggi']");
            String[] dateUtili = new String[giornoPrevisione.size()];
            String tempMaxGiorno = "0";
            String tempMinGiorno = "0";

            if(data.substring(0,2).equals(now.toString().substring(8,10))){
                for (int i = 0; i < tempMaxOggi.size(); i++) {
                    int num1 = Integer.parseInt(tempMaxOggi.get(i).text());
                    int num2 = Integer.parseInt(tempMaxGiorno);
                    if (i < 4) {
                        tempMaxGiorno = tempMaxOggi.get(i).text();
                        if (num2 > num1) {
                            tempMaxGiorno = String.valueOf(num2);
                            tempMinGiorno = String.valueOf(num1);
                        } else {
                            tempMaxGiorno = String.valueOf(num1);
                            tempMinGiorno = String.valueOf(num2);
                        }

                    }
                }
                tempMax = tempMaxGiorno;
            }
            for (int i = 0; i < giornoPrevisione.size(); i++) {
                String date = giornoPrevisione.get(i).text();
                dateUtili[i]=date.substring(4,6);
                if(i==0) //nel caso il giorno sia il giorno corrente
                    dateUtili[0]=oggi;
                else if(i==1) //nel caso in cui la data sia il giorno seguente nel momento della richiesta della previsione, in quanto 3bmeteo per riferirsi al giorno seguente usa "domani")
                    dateUtili[1]=domani;
                else //tutto il resto dei casi
                    dateUtili[i]=date.substring(4,6);
            }
            for (int i = 0; i < temperatura.size(); i++) {
                String temperaturaP = temperatura.get(i).text();
                if(i<28){ //dopo 28 righe le temperature non sono piu necessarie
                    temp[i]= Integer.parseInt(temperaturaP);
                }
            }
            for (int i = 0, c=-2; i < dateUtili.length; i++, c+=2) {
                if (data.substring(0, 2).equals(dateUtili[i]) && data.substring(0,2).equals(now.toString().substring(8,10))==false ) {
                    int temp1= temp[c];
                    int temp2 = temp[c+1];
                    if(temp1>temp2)
                        tempMax= String.valueOf(temp1);
                    else
                        tempMax= String.valueOf(temp2);

                }

            }
            result = Integer.parseInt(tempMax);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int getMeteoTempMinGiorno(String citta, String data) {
        String tempMin = "";
        String urlDomani = "https://www.3bmeteo.com/meteo/" + citta + "/1";

        int result;
        try {
            Document document = Jsoup.connect(url + citta).get();
            Document documentAlt = Jsoup.connect(urlDomani).get();
            int[] temp = new int[28];
            Elements giornoPrevisione = document.select(".navDays");
            Elements temperatura = document.select(".switchcelsius");
            Elements tempMaxOggi = documentAlt.select("[title='oggi'] .switchcelsius");
            String[] dateUtili = new String[giornoPrevisione.size()];
            String tempMaxGiorno = "0";
            String tempMinGiorno = "0";

            if (data.substring(0, 2).equals(now.toString().substring(8, 10))) {
                for (int i = 0; i < tempMaxOggi.size(); i++) {
                    int num1 = Integer.parseInt(tempMaxOggi.get(i).text());
                    int num2 = Integer.parseInt(tempMaxGiorno);
                    if (i < 4) {
                        tempMaxGiorno = tempMaxOggi.get(i).text();
                        if (num2 > num1) {
                            tempMaxGiorno = String.valueOf(num2);
                            tempMinGiorno = String.valueOf(num1);
                        } else {
                            tempMaxGiorno = String.valueOf(num1);
                            tempMinGiorno = String.valueOf(num2);
                        }

                    }
                }
                tempMin = tempMinGiorno;
            }
            for (int i = 0; i < giornoPrevisione.size(); i++) {
                String date = giornoPrevisione.get(i).text();
                dateUtili[i] = date.substring(4, 6);
                if (i == 0) //nel caso il giorno sia il giorno corrente
                    dateUtili[0] = oggi;
                else if (i == 1) //nel caso in cui la data sia il giorno seguente nel momento della richiesta della previsione, in quanto 3bmeteo per riferirsi al giorno seguente usa "domani")
                    dateUtili[1] = domani;
                else //tutto il resto dei casi
                    dateUtili[i] = date.substring(4, 6);
            }
            for (int i = 0; i < temperatura.size(); i++) {
                String temperaturaP = temperatura.get(i).text();
                if (i < 28) { //dopo 28 righe le temperature non sono piu necessarie
                    temp[i] = Integer.parseInt(temperaturaP);
                }
            }
            for (int i = 0, c = -2; i < dateUtili.length; i++, c += 2) {
                if (data.substring(0, 2).equals(dateUtili[i]) && data.substring(0, 2).equals(now.toString().substring(8, 10)) == false) {
                    int temp1 = temp[c];
                    int temp2 = temp[c + 1];
                    if (temp1 < temp2)
                        tempMin = String.valueOf(temp1);
                    else
                        tempMin = String.valueOf(temp2);

                }

            }
            result = Integer.parseInt(tempMin);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static String getMeseAnno(String data){
        Instant now=Instant.now();
        String mese=data.substring(3,5);
        if(mese.equals("01"))
            mese="Gennaio"+" "+now.toString().substring(0,4);
        else if(mese.equals("02"))
            mese="Febbraio"+" "+now.toString().substring(0,4);
        else if(mese.equals("03"))
            mese="Marzo"+" "+now.toString().substring(0,4);
        else if(mese.equals("04"))
            mese="Aprile"+" "+now.toString().substring(0,4);
        else if(mese.equals("05"))
            mese="Maggio"+" "+now.toString().substring(0,4);
        else if(mese.equals("06"))
            mese="Giugno"+" "+now.toString().substring(0,4);
        else if(mese.equals("07"))
            mese="Luglio"+" "+now.toString().substring(0,4);
        else if(mese.equals("08"))
            mese="Agosto"+" "+now.toString().substring(0,4);
        else if(mese.equals("09"))
            mese="Settembre"+" "+now.toString().substring(0,4);
        else if(mese.equals("10"))
            mese="Ottobre"+" "+now.toString().substring(0,4);
        else if(mese.equals("11"))
            mese="Novembre"+" "+now.toString().substring(0,4);
        else if(mese.equals("12"))
            mese="Dicembre"+" "+now.toString().substring(0,4);

        return mese;
    }

    public static int getAvgTemperatura(int temp1, int temp2){
        int tempavg;
        tempavg = (temp1 + temp2)/2;
        return tempavg;
    }

}

