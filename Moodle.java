package com.bot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Moodle  {

    public static final String token = "*****";

    // Возврат -1 -> пользователь не найден ; -2 -> field поля не существует
    public static String user_info(String login, String field) throws Exception{

        String url_request = "http://edu.mmcs.sfedu.ru/webservice/rest/server.php?wstoken=" + token + "&wsfunction=core_user_get_users_by_field&field=username&values[0]="
                + login + "&moodlewsrestformat=json";

        URL call = new URL(url_request);

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(call.openStream()));

            String call_return = in.readLine();
            in.close();

            JSONParser parser = new JSONParser();

            JSONObject obj = (JSONObject) ((JSONArray) parser.parse(call_return)).get(0);
            try { return obj.get(field).toString(); }
            catch (Exception e) { return "-2"; }
        }
        catch (Exception e) { return "-1"; }


    }

}
