package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CSVToMySQL {
    public static void importToMySQL(String csvFile) {
        String dbUrl = "jdbc:mysql://localhost:3306/ilaclardb";
        String username = "root";
        String password = "12345678";

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
             CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {

            String[] parts;
            boolean headerSkipped = false;
            while ((parts = csvReader.readNext()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                String ilacIsmi = parts[1];
                String soru = parts[2];
                String cevap = parts[3];

                insertIntoDB(connection,id, ilacIsmi, soru, cevap);
            }

            System.out.println("Veriler başarıyla MySQL'e aktarıldı.");

        } catch (SQLException | IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private static void insertIntoDB(Connection connection,int id, String ilacIsmi,  String soru, String cevap) {
        String sql = "INSERT INTO ilac (ilac_id,ilac_ismi,  soru, cevap) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, ilacIsmi);
            statement.setString(3, soru);
            statement.setString(4, cevap);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}