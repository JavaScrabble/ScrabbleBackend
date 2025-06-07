package org.example.dictionary;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DictionaryService implements DictionaryProvider{

    public boolean doesWordExist(String word) {
        try {
            URL url = URI.create("https://api.dictionaryapi.dev/api/v2/entries/en/" + word).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
