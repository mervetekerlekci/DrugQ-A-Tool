package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;


public class Main {
    private static final String[] PDF_PATHS = {
            "/Users/mervetekerlekci/Desktop/katarin.pdf",
            "/Users/mervetekerlekci/Desktop/Coldaway.pdf",
            "/Users/mervetekerlekci/Desktop/Arveles.pdf",
            "/Users/mervetekerlekci/Desktop/vermidon.pdf",
            "/Users/mervetekerlekci/Desktop/parol.pdf",
             "/Users/mervetekerlekci/Desktop/18711-redox-c-ampul-500-mg-5-ml-iv-enjeksiyonluk-cozelti-kub.pdf",
              "/Users/mervetekerlekci/Desktop/5325-nac-600-efervesan-tablet-kub.pdf",
              "/Users/mervetekerlekci/Desktop/10099-oseflu-30mg-sase-kub.pdf",

             "/Users/mervetekerlekci/Desktop/karvea-150mg-2023-05-04-KUB.pdf",
             "/Users/mervetekerlekci/Desktop/ALVASTN40mgfilmtabletKB_ddabd0dc-7531-45e0-a8bd-cd291186649a.pdf",
             "/Users/mervetekerlekci/Desktop/kb_e01926cc-5f25-41c8-8a0f-c6298a73c121.pdf",
             "/Users/mervetekerlekci/Desktop/yayınlanacak-küb-nexium 40.pdf_a0195cf9-272d-4639-ad81-5b84dc097ecc.pdf",
             "/Users/mervetekerlekci/Desktop/vidaptinmet50850ksarnbilgisi_3bec1357-0cf7-4ee4-95e9-aff862617208.pdf",
             "/Users/mervetekerlekci/Desktop/kaps küb.pdf_1302e93d-f2cc-43f1-ae71-d1480083eb19.pdf",
             "/Users/mervetekerlekci/Desktop/yaynsalofalk250mgenteriktabletkub_e91f1478-91ed-4616-9369-51530df807c8.pdf",
                "/Users/mervetekerlekci/Desktop/3523-cabral-400-mg-film-tablet-kub.pdf",
                "/Users/mervetekerlekci/Desktop/Aerius-D12-Tablet_KUB_Final.pdf",
                "/Users/mervetekerlekci/Desktop/20820-candidin-50-mg-kapsul-kub.pdf",
                "/Users/mervetekerlekci/Desktop/11808-aksef-500-mg-film-tablet-kub.pdf",
                "/Users/mervetekerlekci/Desktop/Voltaren50mg-KUB-16112022.pdf",
                "/Users/mervetekerlekci/Desktop/dideral.pdf",
                "/Users/mervetekerlekci/Desktop/diclomec.pdf",
                "/Users/mervetekerlekci/Desktop/pulcet.pdf",
                "/Users/mervetekerlekci/Desktop/ir5ViI0HMRu5w.pdf",
                "/Users/mervetekerlekci/Desktop/25072016175020.pdf",
                "/Users/mervetekerlekci/Desktop/16317-cefiten-400-mg-film-kapli-tablet-kub.pdf",
                "/Users/mervetekerlekci/Desktop/6c09e46575433.pdf",
                "/Users/mervetekerlekci/Desktop/4206-cipralex-10-mg-film-tablet-kub.pdf",
                "/Users/mervetekerlekci/Desktop/zoretanin.pdf",
                "/Users/mervetekerlekci/Desktop/6759-enapril-5mg-tablet-kub.pdf",







    };

    private static final Map<Integer, String> ID_TO_ILAC_ISMI = new HashMap<>();

    static {
        ID_TO_ILAC_ISMI.put(1, "Katarin");
        ID_TO_ILAC_ISMI.put(2, "Coldaway");
        ID_TO_ILAC_ISMI.put(3, "Arveles");
        ID_TO_ILAC_ISMI.put(4, "vermidon");
        ID_TO_ILAC_ISMI.put(5, "parol");
        ID_TO_ILAC_ISMI.put(6, "redoxon");
        ID_TO_ILAC_ISMI.put(7, "nac");
        ID_TO_ILAC_ISMI.put(8, "oseflu");
        ID_TO_ILAC_ISMI.put(9, "Karvea ");
        ID_TO_ILAC_ISMI.put(10, "Alvastin ");
        ID_TO_ILAC_ISMI.put(11, "Tromboxar ");
        ID_TO_ILAC_ISMI.put(12, "Nexium ");
        ID_TO_ILAC_ISMI.put(13, "Vidaptin ");
        ID_TO_ILAC_ISMI.put(14, "Ursofalk ");
       ID_TO_ILAC_ISMI.put(15, "Salofalk ");
        ID_TO_ILAC_ISMI.put(16, "Cabral ");
        ID_TO_ILAC_ISMI.put(17, "Aerius ");
        ID_TO_ILAC_ISMI.put(18, "Candidin ");
        ID_TO_ILAC_ISMI.put(19, "Aksef ");
        ID_TO_ILAC_ISMI.put(20, "Voltaren ");
        ID_TO_ILAC_ISMI.put(21, "Dideral ");
        ID_TO_ILAC_ISMI.put(22, "Diclomec ");
        ID_TO_ILAC_ISMI.put(23, "Pulcet ");
        ID_TO_ILAC_ISMI.put(24, "Delix ");
        ID_TO_ILAC_ISMI.put(25, "Detofen ");
        ID_TO_ILAC_ISMI.put(26, "Cefiten ");
        ID_TO_ILAC_ISMI.put(27, "Ebetaxel ");
        ID_TO_ILAC_ISMI.put(28, "Cipralex ");
        ID_TO_ILAC_ISMI.put(29, "  zoretanin ");
        ID_TO_ILAC_ISMI.put(30, "  enapril ");








    }



    private static final String dbUrl = "jdbc:mysql://localhost:3306/ilaclardb";
    private static final String username = "root";
    private static final String password = "12345678";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                İlacArama window = new İlacArama();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            Sorular[] sorularArray = extractQuestionsFromPDFs();

            CSVExporter.exportToCSV("/Users/mervetekerlekci/Desktop/output.csv", sorularArray);

            CSVToMySQL.importToMySQL("/Users/mervetekerlekci/Desktop/output.csv");


            displayMenuAndSearchForInput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Sorular[] extractQuestionsFromPDFs() throws IOException {
        Sorular[] sorularArray = new Sorular[PDF_PATHS.length];
        for (int i = 0; i < PDF_PATHS.length; i++) {
            File pdfFile = new File(PDF_PATHS[i]);
            String ilacIsmi = ID_TO_ILAC_ISMI.get(i + 1);
            sorularArray[i] = extractAnswersFromPDF(pdfFile, ilacIsmi);
        }
        return sorularArray;
    }

    private static void displayMenuAndSearchForInput() {
        Scanner scanner = new Scanner(System.in);
        int choice;



        do {
            System.out.println("Sorular Menüsü:");
            for (int i = 0; i < Sorular.QUESTIONS.length; i++) {
                System.out.println((i + 1) + ". " + Sorular.QUESTIONS[i]);
            }
            System.out.println("0. Çıkış");

            System.out.print("Lütfen bir seçim yapınız (0-" + Sorular.QUESTIONS.length + "): ");
            choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("Programdan çıkılıyor...");
            } else if (choice < 1 || choice > Sorular.QUESTIONS.length) {
                System.out.println("Geçersiz seçim. Lütfen tekrar deneyin.");
            } else {
                String selectedQuestion = Sorular.QUESTIONS[choice - 1];
                System.out.println("Seçilen soru: " + selectedQuestion);

                if (choice == 6) {
                    selectMedicineAndDisplayAnswer(scanner);
                } else {
                    System.out.print("Lütfen bir input giriniz: ");
                    scanner.nextLine(); // Bu satır, önceki nextInt() çağrısından kalan newline karakterini temizler.
                    String input = scanner.nextLine();

                    try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
                        List<String> eslesenIlaclar = searchInputInAnswers(connection, selectedQuestion, input);
                        if (eslesenIlaclar.isEmpty()) {
                            System.out.println("Bu inputa sahip ilaç bulunamadı.");
                        } else {
                            if (eslesenIlaclar.size() == 1) {
                                System.out.println("Bu inputu içeren ilaç:");
                                System.out.println(eslesenIlaclar.get(0));
                            } else {
                                System.out.println("Bu inputu içeren ilaçların isimleri:");
                                for (int i = 0; i < eslesenIlaclar.size(); i++) {
                                    System.out.println((i + 1) + ". " + eslesenIlaclar.get(i));
                                }

                                System.out.print("Hangi ilacı seçmek istiyorsunuz? (1-" + eslesenIlaclar.size() + "): ");
                                int ilacSecim = scanner.nextInt();
                                if (ilacSecim >= 1 && ilacSecim <= eslesenIlaclar.size()) {
                                    System.out.println("Seçilen ilaç: " + eslesenIlaclar.get(ilacSecim - 1));
                                } else {
                                    System.out.println("Geçersiz ilaç seçimi.");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println();
        } while (choice != 0);
    }

    private static void selectMedicineAndDisplayAnswer(Scanner scanner) {
        System.out.println("İlaç Seçimi:");
        int counter = 1;
        for (String ilacIsmi : ID_TO_ILAC_ISMI.values()) {
            System.out.println(counter + ". " + ilacIsmi);
            counter++;
        }

        System.out.print("Lütfen bir ilaç seçiniz (1-" + (counter - 1) + "): ");
        int medicineChoice = scanner.nextInt();

        if (medicineChoice < 1 || medicineChoice > counter - 1) {
            System.out.println("Geçersiz seçim.");
            return;
        }

        String selectedMedicine = ID_TO_ILAC_ISMI.get(medicineChoice);
        String answer = getAnswerForQuestionSix(selectedMedicine);

        if (answer.isEmpty()) {
            System.out.println("Doz aşımı hakkında bilgi bulunamadı.");
        } else {
            System.out.println("Seçilen ilacın ( " + selectedMedicine + " ) doz aşımı olduğunda : ");
            System.out.println(answer);
        }
    }

    private static String getAnswerForQuestionSix(String selectedMedicine) {
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            String query = "SELECT cevap FROM ilac WHERE ilac_ismi = ? AND LOWER(soru) LIKE LOWER(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedMedicine);
            preparedStatement.setString(2, "%" + Sorular.QUESTIONS[5].toLowerCase() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("cevap");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static List<String> searchInputInAnswers(Connection connection, String soru, String input) throws SQLException {
        List<String> ilacIsimleri = new ArrayList<>();
        String query = "SELECT DISTINCT ilac_ismi FROM ilac WHERE LOWER(soru) LIKE LOWER(?) AND LOWER(cevap) LIKE LOWER(?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + soru.toLowerCase() + "%");
            preparedStatement.setString(2, "%" + input.toLowerCase() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ilacIsimleri.add(resultSet.getString("ilac_ismi"));
            }
        }
        return ilacIsimleri;
    }

    private static Sorular extractAnswersFromPDF(File file, String ilacIsmi) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String content = pdfStripper.getText(document);
        document.close();
        return new Sorular(ilacIsmi, content);
    }
}



