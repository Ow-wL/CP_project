package owl.copy;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.*;
import java.net.URL;
import java.util.*;

import com.formdev.flatlaf.FlatLightLaf;

public class GUI extends JFrame {
    private static String SAVE_FILE = Config.getButtonJsonPath();
    private Map<String, String> buttonData = new LinkedHashMap<>();
    private Map<String, JButton> buttonMap = new LinkedHashMap<>();
    private JPanel mainPanel;
    private JTextArea statusTextArea;

    private enum Mode { NONE, REMOVE, EDIT }
    private Mode currentMode = Mode.NONE;

    private int TextAreaHeight = 100;

    public static void main(String[] args) {
        GUI frame = new GUI("Copy_OWL");
    }

    public GUI(String title) {
        try {
            // 컨텐츠 루트 기준 경로로 수정
            String iconPath = "/icon.png";
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                Image appIcon = new ImageIcon(iconUrl).getImage();
                this.setIconImage(appIcon);
                System.out.println("애플리케이션 아이콘이 성공적으로 설정되었습니다.");
            } else {
                System.err.println("애플리케이션 아이콘 파일을 찾을 수 없습니다: " + iconPath);
            }
        } catch (Exception e) {
            System.err.println("애플리케이션 아이콘 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.trackInsets", new Insets(0, 0, 0, 0));
            UIManager.put("ScrollBar.trackColor", new Color(240, 240, 240));
            UIManager.put("ScrollBar.thumbColor", new Color(180, 180, 180));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle(title);
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Settings");
        JMenuItem m1_0 = new JMenuItem("Insert Button");
        JMenuItem m1_1 = new JMenuItem("Remove Button");
        JMenuItem m1_2 = new JMenuItem("Edit Button");
        JMenuItem m1_3 = new JMenuItem("Open Data File");
        JMenuItem m1_5 = new JMenuItem("Set Data Directory");
//        JMenuItem m1_4 = new JMenuItem("Text Area Scale");

        JMenu m2 = new JMenu("Refresh");
        JMenuItem m2_0 = new JMenuItem("Refresh Button");

        JMenu m3 = new JMenu("Help");
        JMenuItem m3_0 = new JMenuItem("Info");

        m1.add(m1_0);
        m1.add(m1_1);
        m1.add(m1_2);
        m1.add(m1_3);
//        m1.add(m1_4);
        m1.add(m1_5);
        m2.add(m2_0);
        m3.add(m3_0);

        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        setJMenuBar(mb);

        // 상태 메시지 영역
        statusTextArea = new JTextArea();
        statusTextArea.setLineWrap(true);
        statusTextArea.setWrapStyleWord(true);
        statusTextArea.setEditable(false);
        statusTextArea.setBackground(new Color(240, 240, 240));
        statusTextArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        statusTextArea.setFont(new Font("Dialog", Font.PLAIN, 12));

        JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
        statusScrollPane.setBounds(10, 10, 560, TextAreaHeight);
        add(statusScrollPane);

        // 버튼 패널
        mainPanel = new JPanel(null);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBounds(10, 110, 560, 500);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        m1_0.addActionListener(e -> openInsertDialog(null, null));

        m1_1.addActionListener(e -> {
            currentMode = Mode.REMOVE;
            JOptionPane.showMessageDialog(null, "삭제할 버튼을 클릭하세요.", "버튼 삭제", JOptionPane.INFORMATION_MESSAGE);
        });

        m1_2.addActionListener(e -> {
            currentMode = Mode.EDIT;
            JOptionPane.showMessageDialog(null, "수정할 버튼을 클릭하세요.", "버튼 수정", JOptionPane.INFORMATION_MESSAGE);
        });

        m1_3.addActionListener(e -> {
            try {
                File file = new File(SAVE_FILE);
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(this, "저장된 데이터 파일이 없습니다.");
                    return;
                }
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "파일 열기 오류: " + ex.getMessage());
            }
        });

//        m1_4.addActionListener(e -> {
//            String inputValue = JOptionPane.showInputDialog(null, "텍스트 박스의 높이 값을 입력해주세요.(정수)", "높이 설정", JOptionPane.QUESTION_MESSAGE);
//            if (inputValue != null){
//                if (!inputValue.trim().isEmpty()) {
//                    try {
//                        int parsedHeight = Integer.parseInt(inputValue.trim());
//                        if(parsedHeight <= 0) {
//                            JOptionPane.showMessageDialog(null, "'" + inputValue + "'은 높이로 설정될 수 없습니다.", "입력값 오류", JOptionPane.ERROR_MESSAGE);
//                        }
//                        else {
//                            TextAreaHeight = parsedHeight;
//                            statusScrollPane.setBounds(statusScrollPane.getX(), statusScrollPane.getY(), statusScrollPane.getWidth(), TextAreaHeight);
//                            getContentPane().revalidate();
//                            getContentPane().repaint();
//                            JOptionPane.showMessageDialog(null,  "텍스트 박스 높이가 "+ TextAreaHeight + "로 설정 되었습니다.", "높이 변경", JOptionPane.INFORMATION_MESSAGE);
//                        }
//                    } catch (NumberFormatException e2) {
//                        JOptionPane.showMessageDialog(null, "'" + inputValue + "'은(는) 유효한 정수가 아닙니다.", "입력값 오류", JOptionPane.ERROR_MESSAGE);
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(null, "값이 입력되지 않았습니다.", "입력값 오류", JOptionPane.WARNING_MESSAGE);
//                }
//            } else {
//                JOptionPane.showMessageDialog(null, "입력이 취소되었습니다.", "입력값 오류", JOptionPane.WARNING_MESSAGE);
//            }
//        });

        // 데이터 파일 경로 지정
        m1_5.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                JFileChooser fileChooser = new JFileChooser();

                // .json 파일만 보이도록 설정
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

                fileChooser.setDialogTitle("JSON 파일 선택");
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    System.out.println("선택된 파일 경로: " + path);
                    JOptionPane.showMessageDialog(null, "데이터 파일이 선택되었습니다! \n경로 : " + path, "JSON 파일 선택 완료", JOptionPane.INFORMATION_MESSAGE);
                    SAVE_FILE = path;
                    Config.setButtonJsonPath(SAVE_FILE); // 경로 저장
                    refreshButtons();
                } else {
                    System.out.println("파일 선택이 취소되었습니다.");
                    JOptionPane.showMessageDialog(null, "데이터 파일 선택이 취소되었습니다.", "JSON 파일 선택 취소", JOptionPane.WARNING_MESSAGE);
                }
            });
        });

        // 버튼 새로고침
        m2_0.addActionListener(e -> {
            loadButtonsFromFile();
            refreshButtons();
            JOptionPane.showMessageDialog(null, "모든 버튼이 새로고침 되었습니다!", "버튼 새로고침", JOptionPane.INFORMATION_MESSAGE);
        });
        loadButtonsFromFile();
        setResizable(false);
        setVisible(true);
    }

    private void openInsertDialog(String oldName, String oldValue) {
        JTextField nameField = new JTextField(oldName != null ? oldName : "");
        JTextArea valueArea = new JTextArea(oldValue != null ? oldValue : "", 5, 20);
        valueArea.setLineWrap(true);
        valueArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(valueArea);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("버튼 이름"), BorderLayout.NORTH);
        panel.add(nameField, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "버튼 정보 입력", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String value = valueArea.getText();

            if (!name.isEmpty() && !value.isEmpty()) {
                if (oldName != null && !oldName.equals(name)) {
                    buttonMap.remove(oldName);
                    buttonData.remove(oldName);
                }

                buttonMap.put(name, null);
                buttonData.put(name, value);

                refreshButtons();
                saveButtonsToFile();
            }
        }
    }

    private void refreshButtons() {
        mainPanel.removeAll();
        buttonMap.clear();

        int x = 13, y = 10;
        int count = 0;

        for (Map.Entry<String, String> entry : buttonData.entrySet()) {
            JButton btn = createButton(entry.getKey(), entry.getValue());
            btn.setBounds(x, y, 125, 35);
            mainPanel.add(btn);
            buttonMap.put(entry.getKey(), btn);

            x += 135;
            count++;
            if (count % 4 == 0) {
                x = 13;
                y += 45;
            }
        }

        mainPanel.setPreferredSize(new Dimension(550, y + 50));
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JButton createButton(String name, String value) {
        JButton btn = new JButton(name);
        btn.setBackground(new Color(158, 198, 243));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(5, 10, 5, 10));
        btn.setFont(new Font("Dialog", Font.PLAIN, 12));

        btn.addActionListener(e -> {
            if (currentMode == Mode.REMOVE) {
                int res = JOptionPane.showConfirmDialog(this, "정말 '" + name + "' 버튼을 삭제할까요?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    buttonMap.remove(name);
                    buttonData.remove(name);
                    refreshButtons();
                    saveButtonsToFile();
                    JOptionPane.showMessageDialog(null, name + " 버튼이 삭제되었습니다.", "버튼 삭제", JOptionPane.INFORMATION_MESSAGE);
                }
                currentMode = Mode.NONE;
            } else if (currentMode == Mode.EDIT) {
                currentMode = Mode.NONE;
                openInsertDialog(name, value);
            } else {
                copyToClipboard(value);
                statusTextArea.setText(value);
                statusTextArea.setCaretPosition(0);
            }
        });

        return btn;
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(text);
        clipboard.setContents(selection, null);
    }

    private void saveButtonsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write("{\n");
            int i = 0;
            int size = buttonData.size();
            for (Map.Entry<String, String> entry : buttonData.entrySet()) {
                writer.write("  \"" + escapeJson(entry.getKey()) + "\": \"" + escapeJson(entry.getValue()) + "\"");
                if (++i < size) writer.write(",");
                writer.write("\n");
            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void loadButtonsFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;

        buttonData.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonText.append(line.trim());
            }

            String json = jsonText.toString();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
            }

            String[] pairs = json.split("(?<!\\\\)\",\\s*\"");
            for (String pair : pairs) {
                String[] kv = pair.split("(?<!\\\\)\":\\s*\"");
                if (kv.length == 2) {
                    String key = kv[0].replaceAll("^\\s*\"", "").replace("\\\"", "\"").replace("\\n", "\n");
                    String value = kv[1].replaceAll("\"$", "").replace("\\\"", "\"").replace("\\n", "\n");
                    buttonData.put(key, value);
                }
            }
            refreshButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
