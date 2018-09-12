package com.bot;

import java.sql.*;
import java.util.LinkedList;
import java.util.Stack;

public class Base {

    public static String url = "C:\\Users\\Эдуард\\IdeaProjects\\Bot\\src\\com\\bot\\DataBase\\database.db";
    public static String tableUsers = "users";
    public static String tableNews = "news";
    public static String tableNewsSend = "news_send";
    public static Connection connection;

    public static void connect()
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + url);
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    // USERS ____________________________________________________________________________________________________________________________________________________

    public static String searchUser(String vk_id) throws  SQLException{
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableUsers + " WHERE vk_id = \"" + vk_id + "\"");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return resultSet.getString("moodle_login");
        else
            return "-1";
    }

    public static String get_user_id_by_moodle_login(String moodle_login) throws  SQLException{
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableUsers + " WHERE moodle_login = \"" + moodle_login + "\"");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return resultSet.getString("vk_id");
        else
            return "-1";
    }

    //user_id - id Вконтакте, params: 0 - логин Moodle, 1 - курс, 2 - группа
    public static void addUser(String user_id, String[] params) throws  SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableUsers +
                "(vk_id, moodle_login, course, team) VALUES (?, ?, ?, ?)");

        statement.setString(1, user_id);
        statement.setString(2, params[0]);
        statement.setInt(3, Integer.parseInt(params[1]));
        statement.setInt(4, Integer.parseInt(params[2]));

        statement.executeUpdate();

        statement = connection.prepareStatement("INSERT INTO " + tableNewsSend +
                "(vk_id, mmcs) VALUES (?, ?)");

        statement.setString(1, user_id);
        statement.setInt(2, 1);

        statement.executeUpdate();
    }

    // NEWS ____________________________________________________________________________________________________________________________________________________

    public static int searchNews(String start_text) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableNews + " WHERE start_text = \"" + start_text + "\"");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
            return 1;
        else
            return -1;
    }

    public static void writeNews(Stack<String> news) throws SQLException {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableNews);
            statement.executeUpdate();

            for (String text : news) {
                statement = connection.prepareStatement("INSERT INTO " + tableNews +
                        "(start_text) VALUES (?)");

                statement.setString(1, text.substring(0, 50));
                statement.executeUpdate();
            }
    }

    // NEWS_SEND ____________________________________________________________________________________________________________________________________________________

    public static LinkedList<String> news_send_getUsers(String field) throws  SQLException {
        LinkedList<String> users = new LinkedList<>();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableNewsSend + " WHERE " + field + " = 1");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next())
            users.add(resultSet.getString("vk_id"));

        return users;
    }
}
