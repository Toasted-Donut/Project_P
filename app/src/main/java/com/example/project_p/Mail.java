package com.example.project_p;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

class Mail extends Application{
    String user;
    String password;
    String host;
    int port;
    int msgs_count;
    Properties props;
    ArrayList<Check> checks;

    public Mail(Properties mailProps){
        user = mailProps.getProperty("mail.imap.user");
        password = mailProps.getProperty("mail.imap.password");
        host = mailProps.getProperty("mail.imap.host");
        port = Integer.parseInt(mailProps.getProperty("mail.imap.port"));
        props = mailProps;
        checks = new ArrayList<Check>();
    }
    public Mail(){
        //only for load from settings
    }
    public void getMessages(){
        Store store = null;
        try {
            store = Session.getInstance(props).getStore();
            Log.i("prop",host+" "+port+" "+user+" "+password);

            store.connect(host, port, user, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] msgs = inbox.getMessages();
            msgs_count = msgs.length;

            for (int i = 0; i < msgs.length; i++) {
                if(this.getTextFromMessage(msgs[i])!=null){
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(MyApplication.getAppContext().openFileOutput(String.format("out%d.html",i),MODE_PRIVATE)));
                    objectOutputStream.writeObject(getTextFromMessage(msgs[i]));
                    objectOutputStream.close();
                }
            }
            inbox.close(true);
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getData() throws IOException {
        checks = new ArrayList<Check>();
        String path_buf;
        for (int i = 3; i < msgs_count; i++) {
            path_buf = String.format("out%d.html",i);
            FileInputStream fileInputStream = MyApplication.getAppContext().openFileInput(path_buf);
            Document doc = Jsoup.parse(readFromInputStream(fileInputStream));
            Check check = new Check(doc.select(":containsOwn(Дата | Время) + td").text(), path_buf);
            try{
                int c = 0;
                while(true){
                    check.items.add(new Item(
                            doc.select("span[style=\"line-height: 21px; color: #000000; font-weight: bold;\"]").get(c*2 + 1).text(),
                            Float.parseFloat(doc.select(":containsOwn(Сумма) + td").get(c).text())));
                    c++;
                }
            }
            catch (Exception ignored){
            }
            checks.add(check);
            fileInputStream.close();
        }
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Mail fromJson(String json){
        if(json.equals("")){
            return null;
        }
        return new Gson().fromJson(json, Mail.class);
    }

    public void save(Context context){
        String json = toJson();
        SharedPreferences settings = context.getSharedPreferences("settings",MODE_PRIVATE);
        settings.edit().putString("mail_json",json).apply();
    }

    public Mail load(Context context){
        SharedPreferences settings = context.getSharedPreferences("settings",MODE_PRIVATE);
        return fromJson(settings.getString("mail_json",""));
    }
    private Object getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("multipart/*")) {
            return  ((MimeMultipart)((MimeMultipart)message.getContent()).getBodyPart(0).getContent()).getBodyPart(0).getContent();
        }
        return null;
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


    static class Check{
        String date;
        String filePath;
        long ID; //дата в формате миллисекунд
        ArrayList<Item> items;

        public Check(String date_str, String file_path){
            this.date = date_str;
            filePath = file_path;
            items = new ArrayList<Item>();
            try{
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy | HH:mm");
                Date date = format.parse(date_str);
                ID = date.getTime();
            }
            catch (Exception ex) {
                ID = new Date().getTime(); //today date
            }
        }
    }

    static class Item{
        String name;
        float sum;

        public Item(String name, float sum){
            this.name = name;
            this.sum = sum;
        }
    }
}
