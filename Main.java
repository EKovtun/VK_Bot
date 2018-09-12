package com.bot;

public class Main {
    public static void main(String[] args) throws Exception {

        Server.setLongPollServer();
        Base.connect();

        int i = 3599;

        while (true) {
            Thread.sleep(1000);
            i++;
            Bot.add_messages();
            if (i == 3600) {
                i = 0;
                News.send();
            }
        }
    }
}
