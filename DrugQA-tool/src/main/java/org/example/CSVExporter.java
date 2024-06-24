package org.example;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVExporter {

    public static void exportToCSV(String outputPath, Sorular[] sorularArray) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("id,ilacIsmi,soru,cevap\n");

            for (int i = 0; i < sorularArray.length; i++) {
                Sorular sorular = sorularArray[i];
                String ilacIsmi = sorular.getIlacIsmi();

                for (int j = 0; j < sorular.getSorular().size(); j++) {
                    writer.write((i + 1) + ",");
                    writer.write(ilacIsmi + ",");
                    writer.write("\"" + sorular.getSorular().get(j) + "\",");
                    writer.write("\"" + sorular.getCevaplar().get(j) + "\"\n");
                }
            }

            System.out.println("CSV dosyası başarıyla oluşturuldu: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}