package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sorular {
    private String ilacIsmi;
    private List<String> sorular = new ArrayList<>();
    private List<String> cevaplar = new ArrayList<>();

    public static final String[] QUESTIONS = {
            "Aramak istediginiz etkin maddeyi giriniz:",
            "Aramak istediginiz ilaç formu nedir?",
            "Rahatsızlığınız nedir?",
            "Kullanım önerisi almak istediğiniz ilacın adını giriniz:",
            "Seçtiğiniz ilaç hangi durumlarda kullanılmamalı?",
            "Doz aşımında ne olur?",
            "Aradığız ilaçtaki yardımcı maddeyi giriniz:",
            "İlaçlarda aradığınız raf ömrünü giriniz:",
            "İlaç hangi koşullarda saklanmalıdır?"
    };

    public Sorular(String ilacIsmi, String content) {
        this.ilacIsmi = ilacIsmi;
        String[][] targetSections = {
                {"2\\.", "3\\."},
                {"3\\.", "4\\.1\\."},
                {"4\\.1\\.", "4\\.2\\."},
                {"4\\.2\\.", "4\\.3\\.|4\\.2(.*?)4\\.3"},
                {"4\\.3\\.", "4\\.4\\.|4\\.3(.*?)4\\.4"},
                {"4\\.9\\.", "5\\."},
                {"6\\.1\\.", "6\\.2\\."},
                {"6\\.3\\.", "6\\.4\\."},
                {"6\\.4\\.", "6\\.5\\."},
        };

        for (int i = 0; i < targetSections.length; i++) {
            String startHeading = targetSections[i][0];
            String endHeading = targetSections[i][1];
            String extractedContent = extractContentBetweenHeadings(content, startHeading, endHeading);

            if (!extractedContent.isEmpty()) {
                String cleanedContent = cleanImportantStrings(extractedContent);
                addQuestionAnswer(QUESTIONS[i], cleanedContent.trim());
            } else {
                addQuestionAnswer(QUESTIONS[i], "Belirtilen başlık arasında içerik bulunamadı: " + startHeading + " - " + endHeading);
            }
        }
    }

    private void addQuestionAnswer(String question, String answer) {
        sorular.add(question);
        cevaplar.add(answer.replaceAll("\\r?\\n", " "));
    }
    public List<String> getSorular() {
        return sorular;
    }
    public List<String> getCevaplar() {
        return cevaplar;
    }
    public String getIlacIsmi() {
        return ilacIsmi;
    }
    private static String extractContentBetweenHeadings(String content, String startHeading, String endHeading) {
        String regexPattern = "(?s)" + startHeading + "(.*?)" + endHeading;
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            String icerik = matcher.group(1);
            if (icerik == null || icerik.isEmpty()) {
                icerik = matcher.group(2).trim();
            } else {
                icerik = icerik.trim();
            }
            return icerik;
        }
        return "";
    }

    private static String cleanImportantStrings(String content) {
        String[] importantStrings = {
                "KALİTATİF ve KANTİTATİF BİLEŞİM",
                "KALİTATİF VE KANTİTATİF BİLEŞİM",
                "Terapötik Endikasyonlar",
                "FARMASÖTİK FORM",
                "Terapötik endikasyonlar",
                "Pozoloji ve uygulama şekli",
                "Kontrendikasyonlar",
                "Doz aşımı ve tedavisi",
                "Yardımcı maddelerin listesi",
                "Raf ömrü",
                "Saklamaya yönelik özel tedbirler"
        };

        for (String str : importantStrings) {
            if (content.contains(str)) {
                int index = content.indexOf(str);
                content = content.substring(index + str.length());
                break;
            }
        }

        return content.trim();
    }
}