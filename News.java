package com.bot;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.Stack;

public class News {

    public static void send() throws Exception {

        Document doc = Jsoup.connect("http://mmcs.sfedu.ru//").get();
        Elements elements = doc.getElementsByClass("news_item_f");
        elements.remove(0);

        Stack<String> output_list = new Stack<>();

        for(Element element: elements) {

            String text_article = element.getElementsByClass("article_title").text();
            String text_data = element.getElementsByClass("createdate").text();
            String text_author = element.getElementsByClass("createby").text();
            String text = element.getElementsByClass("newsitem_text").get(0).toString();

            text = text.replaceAll("<[^>]*>", "");
            text = text.replaceAll("\\n", "%0A");

            System.out.println(text.replaceAll("( |%0A)", ""));

            if (text.replaceAll("( |%0A)", "").isEmpty())
                continue;

            if (Base.searchNews(text.substring(0, 50)) == 1)
                break;

            output_list.push(text_article + "%0A" + text_data + "%0A" + text_author + "%0A%0A" + text);
        }

        if (output_list.empty())
            return;

        Base.writeNews(output_list);

        LinkedList<String> users = Base.news_send_getUsers("mmcs");

        while (!output_list.empty()){
            String text = output_list.pop();
            text = text.replaceAll(" ", "%20");

            for(String user : users) {
                Server.send_message_vk(user, "Новости%20мехмата");
                Server.send_message_vk(user, text);
            }
        }
    }

    public static void send_last_news_in_vk(String user_id) throws  Exception {
        Document doc = Jsoup.connect("http://mmcs.sfedu.ru//").get();
        Elements elements = doc.getElementsByClass("newsitem_text");
        String text = elements.get(1).toString();
        text = text.replaceAll("<[^>]*>", "");
        text = text.replaceAll("\\n", "");
        text = text.replaceAll(" ", "%20");

        Server.send_message_vk(user_id, "Новости%20мехмата");
        Server.send_message_vk(user_id, text);
    }
}
