package org.example;
import java.sql.*;

public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/skymeteobot";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed to connect to the database!");
            e.printStackTrace();
        }
        return connection;
    }



    public static String getPrevisione(String citta, String data) {
        int temperaturaMax;
        int temperaturaMin;
        int temperaturaAvg;
        String condizione;
        String result="";
        String query = "INSERT INTO previsioni (citta, data, temperaturaMax, temperaturaMin, condizione) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, citta);
            stmt.setString(2, data);

            temperaturaMax = WebScraper.getMeteoTempMaxGiorno(citta, data);
            temperaturaMin = WebScraper.getMeteoTempMinGiorno(citta, data);
            condizione=WebScraper.getCondizioniMeteo(citta, data);

            stmt.setInt(3, temperaturaMax);
            stmt.setInt(4, temperaturaMin);
            stmt.setString(5, condizione);

            int righe = stmt.executeUpdate();
            System.out.println(righe);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}