package com.shreyansh.TestDb;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionTest {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/web_customer_tracker?useSSL=false&serverTimezone=UTC";
        String user = "springstudent";
        String password = "springstudent";

        try {
            System.out.println("Connecting to database: "+jdbcUrl);
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("Connection Successfull!!!");
        } catch (Exception exec) {
            exec.printStackTrace();
        }
    }
}
