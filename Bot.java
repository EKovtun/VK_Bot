package com.bot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.HashSet;

public class Bot {

    public static HashMap<String, String[]> wait_auth = new HashMap<>();

    public static void add_messages() throws Exception {
        String call_return = Server.get();

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(call_return);
        obj = (JSONObject) obj.get("response");
        obj = (JSONObject) obj.get("messages");

        if ((long) obj.get("count") == 0)
            return;

        Server.setLongPollServer(); //Обновляем входящие сообщения

        JSONArray objArr = (JSONArray) obj.get("items");

        for (Object x: objArr){
            JSONObject message = (JSONObject) x;
            if (message.get("random_id") == null){ //Проверка на входящее сообщение

                String user_id = message.get("user_id").toString();

                if (Base.searchUser(user_id) == "-1") {
                    authorization(message.get("user_id").toString(), message.get("body").toString());
                    continue;
                }

                parse(user_id, message.get("body").toString());
            }
        }
    }

    public static void parse(String user_id, String message) throws Exception{
        if (message.contentEquals("Команды")) {
            Server.send_message_vk(user_id, "Список%20команд:%0A" +
                    "Команды%20-%20показать%20список%20команд;%0A" +
                    "На%20данный%20момент%20остальные%20команды%20отсутствуют.%20Следите%20за%20обновлениями.");

            return;
        }

        Server.send_message_vk(user_id, "Команда%20не%20опознана.%20Проверьте%20корректность%20отправленного%20сообщения%20или%20обратитесь%20к%20списку%20команд:%20Команды.");
    }

    public static void authorization(String user_id, String body) throws Exception{
        if (wait_auth.get(user_id) == null){
            wait_auth.put(user_id, new String[3]);
            Server.send_message_vk(user_id, "Авторизация:");
            Server.send_message_vk(user_id, "Ваш%20логин%20в%20Moodle?");
            return;
        }

        String [] data = wait_auth.get(user_id);

        if (data[0] == null){
            String second_user_id = Base.get_user_id_by_moodle_login(body);
            if (second_user_id != "-1"){
                Server.send_message_vk(user_id, "Этот%20логин%20уже%20занят%20пользователем%20с%20id%20" + second_user_id);
                return;
            }

            String firstname = Moodle.user_info(body, "firstname");
            String lastname = Moodle.user_info(body, "lastname");
            if ((firstname == "-1") || (lastname == "-1"))
                Server.send_message_vk(user_id, "Неверный%20логин.%20Попробуйте%20ещё%20раз.");
            else {
                data[0] = body;
                wait_auth.put(user_id, data);
                Server.send_message_vk(user_id, "Здравствуйте,%20" + lastname + "%20" + firstname +".");
                Server.send_message_vk(user_id, "На%20каком%20курсе%20Вы%20обучаетесь?");
            }
            return;
        }

        if (data[1] == null) {
            if ((body.length() == 0) || (body.length() > 1) || (body.charAt(0) < '1') || (body.charAt(0) > '5')) {
                Server.send_message_vk(user_id, "Неверные%20данные.%20Отправьте%20число%20от%201%20до%205.");
                return;
            }
            data[1] = body;
            wait_auth.put(user_id, data);
            Server.send_message_vk(user_id, "Номер%20Вашей%20группы?");
            return;
        }

        if (data[2] == null) {
            if ((body.length() == 0) || (body.length() > 1) || (body.charAt(0) < '1') || (body.charAt(0) > '9')) {
                Server.send_message_vk(user_id, "Неверные%20данные.%20Отправьте%20число%20от%201%20до%209.");
                return;
            }
            data[2] = body;
            Base.addUser(user_id, data);
            wait_auth.remove(user_id);
            Server.send_message_vk(user_id, "Аутентификация%20успешно%20завершена.");
            News.send_last_news_in_vk(user_id);
            return;
        }
    }
}
