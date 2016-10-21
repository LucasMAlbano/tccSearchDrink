package br.com.alura.searchdrink;

import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Birbara on 24/08/2016.
 */
public class WebClient {

    public static String ApiKey = "AIzaSyAnE8Q44pkOA_ek3gCaS4tATj99LMOuhOM";

    public String post(){

        try{

            String s = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=pt-PT&key=" + ApiKey;
            URL url = new URL(s);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();

            String resposta = new Scanner(connection.getInputStream()).next();

            return resposta;

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
