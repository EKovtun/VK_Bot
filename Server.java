package com.bot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.*;

public class Server {

    public static final String token = "****";
    public static final String versionAPI = "5.73";

    public static String server;
    public static String key;
    public static String ts;

    // Запросить
    // parameters = ["method name", "parameter 1" ... "parameter n"]
    public static String request(String[] parameters) throws Exception {
        String url_request = "https://api.vk.com/method/";

        url_request += parameters[0] + "?";

        for (String param: parameters) {
            if (param != parameters[0])
                url_request += param + "&";
        }

        url_request += "access_token=" + token + "&";
        url_request += "v=" + versionAPI;

        URL call = new URL(url_request);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(call.openStream()));

        String call_return = in.readLine();
        in.close();

        return call_return;
    }

    // Установка данных для подключение к LongPollServer
    public static void setLongPollServer() throws Exception{

        String call_return = Server.request(new String[] {"messages.getLongPollServer", "need_pts=0", "lp_version=0"});

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(call_return);
        obj = (JSONObject) obj.get("response");

        server = obj.get("server").toString();
        key = obj.get("key").toString();
        ts = obj.get("ts").toString();

        System.out.println("ts - " + ts);
    }

    // Запросить обновления
    public static String get() throws Exception {
        return request(new String[] {"messages.getLongPollHistory", "ts=" + ts, "lp_version=0"});
    }

    public  static void send_message_vk(String id, String message) throws Exception {
        Server.request(new String[] {"messages.send", "user_id=" + id, "message=" + message});
    }
}
