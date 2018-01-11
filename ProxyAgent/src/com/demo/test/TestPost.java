package com.demo.test;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TestPost {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            URL url = new URL("http://localhost/HOST_test/post");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Length", "4");
            con.getOutputStream().write("1234".getBytes());
            int code = con.getResponseCode();
            System.out.println("code" + code);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
