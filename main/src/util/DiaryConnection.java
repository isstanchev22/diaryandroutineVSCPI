package util;

import java.sql.*;

public class DiaryConnection {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;" +
                "databaseName=LichenDnevnik;" +
                "user=твоя_потребител;" +
                "password=твоя_парола;" +
                "encrypt=true;" +
                "trustServerCertificate=true;";

        try {
            // Зареждане на драйвера
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Връзка към базата
            Connection conn = DriverManager.getConnection(connectionUrl);
            System.out.println("Успешна връзка!");

        } catch (ClassNotFoundException e) {
            System.out.println("Драйверът не е намерен!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Грешка при връзката:");
            e.printStackTrace();
        }
    }
}