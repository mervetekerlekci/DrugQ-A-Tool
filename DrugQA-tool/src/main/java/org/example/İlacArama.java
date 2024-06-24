package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class İlacArama {
    private JFrame frame;
    private JComboBox<String> questionComboBox;
    private JLabel instructionLabel;
    private JTextField inputField;
    private JButton searchButton;
    private JTextArea resultArea;
    private JScrollPane resultScrollPane;

    private JPanel selectionPanel;
    private JLabel selectionLabel;
    private JTextField selectionField;
    private JButton selectButton;

    private Sorular sorular;

    public İlacArama() {
        frame = new JFrame("İlaç Bilgi Sorgulama");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        String ilacIsmi = "SampleIlac";
        String content = "SampleContent";
        sorular = new Sorular(ilacIsmi, content);

        questionComboBox = new JComboBox<>(sorular.getSorular().toArray(new String[0]));
        topPanel.add(questionComboBox);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instructionLabel = new JLabel("Aramak istediğiniz anahtar kelime :");
        inputField = new JTextField(15); // Boyut belirlendi
        searchButton = new JButton("Ara");
        inputPanel.add(instructionLabel);
        inputPanel.add(inputField);
        inputPanel.add(searchButton);

        resultArea = new JTextArea();
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);

        resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(380, 100));
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectionLabel = new JLabel("Hangi ilacı seçmek istiyorsunuz? (index giriniz):");
        selectionField = new JTextField(5);
        selectButton = new JButton("Seç");

        selectionPanel.add(selectionLabel);
        selectionPanel.add(selectionField);
        selectionPanel.add(selectButton);
        selectionPanel.setVisible(false);

        frame.add(topPanel);
        frame.add(inputPanel);
        frame.add(resultScrollPane);
        frame.add(selectionPanel);

        questionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = questionComboBox.getSelectedIndex();
                if (selectedIndex == 3 || selectedIndex == 4 || selectedIndex == 5 || selectedIndex == 8) {
                    instructionLabel.setText("İlaç İsmi:");
                } else {
                    instructionLabel.setText("Aramak istediğiniz anahtar kelime :");
                }
                frame.revalidate();
                frame.repaint();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedQuestion = (String) questionComboBox.getSelectedItem();
                String userInput = inputField.getText();
                resultArea.setText("");
                selectionPanel.setVisible(false);

                int selectedIndex = questionComboBox.getSelectedIndex();
                if (selectedIndex == 3 || selectedIndex == 4 || selectedIndex == 5 || selectedIndex == 8) {
                    List<String> allAnswers = getSpecificAnswerForQuestion(selectedQuestion, userInput);
                    if (allAnswers.isEmpty()) {
                        resultArea.setText("Bu ilaç için cevap bulunamadı.");
                    } else {
                        StringBuilder resultContent = new StringBuilder();
                        for (String answer : allAnswers) {
                            resultContent.append(answer).append("\n");
                        }
                        resultArea.setText(resultContent.toString());
                    }
                } else {
                    List<String> matchingMedicines = searchInDatabase(selectedQuestion, userInput);
                    if (matchingMedicines.isEmpty()) {
                        resultArea.setText("Bu inputa sahip ilaç bulunamadı.");
                    } else {
                        StringBuilder resultContent = new StringBuilder("Bu inputu içeren ilaçların isimleri:\n");
                        for (int i = 0; i < matchingMedicines.size(); i++) {
                            resultContent.append(i + 1).append(". ").append(matchingMedicines.get(i)).append("\n");
                        }
                        resultArea.setText(resultContent.toString());
                        if (matchingMedicines.size() > 1) {
                            selectionPanel.setVisible(true);
                        }
                    }
                }
                JScrollBar verticalScrollBar = resultScrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        });

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedIndex = Integer.parseInt(selectionField.getText()) - 1;
                    List<String> matchingMedicines = searchInDatabase((String) questionComboBox.getSelectedItem(), inputField.getText());
                    if (selectedIndex >= 0 && selectedIndex < matchingMedicines.size()) {
                        String selectedMedicine = matchingMedicines.get(selectedIndex);
                        resultArea.setText("Seçilen ilaç: " + selectedMedicine);
                        selectionPanel.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Geçersiz indeks, lütfen geçerli bir indeks giriniz.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Geçersiz indeks, lütfen geçerli bir indeks giriniz.");
                }
            }
        });

        frame.setVisible(true);
    }

    private List<String> searchInDatabase(String question, String input) {
        List<String> medicines = new ArrayList<>();
        String dbUrl = "jdbc:mysql://localhost:3306/ilaclardb";
        String username = "root";
        String password = "12345678";

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            String query = "SELECT DISTINCT ilac_ismi FROM ilac WHERE LOWER(soru) LIKE LOWER(?) AND LOWER(cevap) LIKE LOWER(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + question.toLowerCase() + "%");
            preparedStatement.setString(2, "%" + input.toLowerCase() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                medicines.add(resultSet.getString("ilac_ismi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    private List<String> getSpecificAnswerForQuestion(String question, String medicine) {
        List<String> answers = new ArrayList<>();
        String dbUrl = "jdbc:mysql://localhost:3306/ilaclardb";
        String username = "root";
        String password = "12345678";

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            String query = "SELECT cevap FROM ilac WHERE LOWER(soru) LIKE LOWER(?) AND LOWER(ilac_ismi) LIKE LOWER(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + question.toLowerCase() + "%");
            preparedStatement.setString(2, "%" + medicine.toLowerCase() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String answer = resultSet.getString("cevap");
                if (!answers.contains(answer)) {
                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new İlacArama();
            }
        });
    }
}