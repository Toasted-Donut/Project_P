package com.example.project_p;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

public class Statistics extends Application{
    HashMap<GoodsType,Float> sums;
    HashMap<String,Short> dict = new HashMap<>();
    final GoodsType[] types = GoodsType.values();


    public Statistics(){

    }

    public void readMail(Mail mail, long startDate, long endDate){
        InputStream dict_stream;
        try{
            URL u = new URL("https://raw.githubusercontent.com/Toasted-Donut/host/main/dict.json");
            try{
                dict_stream = u.openStream();
            }
            catch (Exception ex){
                dict_stream = MyApplication.getAppContext().getAssets().open("dict.json");
                Log.i("prop","dict_not_found");
            }
            dict = new Gson().fromJson(readFromInputStream(dict_stream),new TypeToken<HashMap<String,Short>>(){}.getType());
        }
        catch (Exception ex){
            Log.i("prop",ex.toString());

        }
        sums = new HashMap<>();
        GoodsType buf;
        for (Mail.Check check : mail.checks) {
            if(check.ID >= startDate && check.ID <= endDate){
                for (Mail.Item item: check.items) {
                    if (!dict.containsKey(item.name)){
                        dict.put(item.name,(short)0);
                    }
                    buf = types[dict.get(item.name)];
                    sums.put(buf,sums.getOrDefault(buf,(float)0)+item.sum);
                }
            }
        }
        Log.i("prop",new Gson().toJson(Statistics.this.sums));
    }
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Statistics fromJson(String json){
        if(json.equals("")){
            return null;
        }
        return new Gson().fromJson(json, Statistics.class);
    }

    public void save(Context context){
        String json = toJson();
        SharedPreferences settings = context.getSharedPreferences("settings",MODE_PRIVATE);
        settings.edit().putString("stat_json",json).apply();
    }

    public Statistics load(Context context){
        SharedPreferences settings = context.getSharedPreferences("settings",MODE_PRIVATE);
        return fromJson(settings.getString("stat_json",""));
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public enum GoodsType{
        OTHER("Другое"),                         // 0
        MILK("Молочная продукция"),              // 1
        CHEESE("Сыры"),                          // 2
        VEGETABLES("Овощи"),                     // 3
        FRUITS("Фрукты"),                        // 4
        BREAD("Хлеб"),                           // 5
        BAKERY("Выпечка"),                       // 6
        CANDY("Сладости"),                       // 7
        PASTA("Макароны"),                       // 8
        SOUSES("Соусы и приправы"),              // 9
        SAUSAGE("Колбасные изделия"),            // 10
        SEMIS("Полуфабрикаты"),                  // 11
        EGGS("Яйца"),                            // 12
        WATER("Вода"),                           // 13
        JUICES("Соки"),                          // 14
        SODA("Газировка");                       // 15
        private String rus;
        GoodsType(String rus){
            this.rus = rus;
        }
        public String getRus(){
            return rus;
        }
    }
}
