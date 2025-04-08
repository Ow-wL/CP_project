package owl.copy;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.*;
import java.util.*;

import com.formdev.flatlaf.FlatLightLaf;

public class GUI extends JFrame {
    private static final String SAVE_FILE = "buttons.json";
    private Map<String, String> buttonData = new LinkedHashMap<>();
    private Map<String, JButton> buttonMap = new LinkedHashMap<>();
    private JPanel mainPanel;
    private JTextArea statusTextArea;

    private enum Mode { NONE, REMOVE, EDIT }
    private Mode currentMode = Mode.NONE;

    public GUI(String title) {
        try {
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

        m1.add(m1_0);
        m1.add(m1_1);
        m1.add(m1_2);
        m1.add(m1_3);
        mb.add(m1);
        setJMenuBar(mb);

        // 상태 메시지 영역
        statusTextArea = new JTextArea();
        statusTextArea.setLineWrap(true);
        statusTextArea.setWrapStyleWord(true);
        statusTextArea.setEditable(false);
        statusTextArea.setBackground(new Color(240, 240, 240));
        statusTextArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        statusTextArea.setFont(new Font("Dialog", Font.PLAIN, 13));

        JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
        statusScrollPane.setBounds(5, 5, 560, 100);
        add(statusScrollPane);

        // 버튼 패널
        mainPanel = new JPanel(null);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBounds(5, 110, 560, 500);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        m1_0.addActionListener(e -> openInsertDialog(null, null));

        m1_1.addActionListener(e -> {
            currentMode = Mode.REMOVE;
            statusTextArea.setText("삭제할 버튼을 클릭하세요.");
        });

        m1_2.addActionListener(e -> {
            currentMode = Mode.EDIT;
            statusTextArea.setText("수정할 버튼을 클릭하세요.");
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

        int x = 5, y = 5;
        int count = 0;

        for (Map.Entry<String, String> entry : buttonData.entrySet()) {
            JButton btn = createButton(entry.getKey(), entry.getValue());
            btn.setBounds(x, y, 120, 35);
            mainPanel.add(btn);
            buttonMap.put(entry.getKey(), btn);

            x += 130;
            count++;
            if (count % 4 == 0) {
                x = 5;
                y += 45;
            }
        }

        mainPanel.setPreferredSize(new Dimension(560, y + 50));
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JButton createButton(String name, String value) {
        JButton btn = new JButton(name);
        btn.setBackground(new Color(52, 192, 234));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Dialog", Font.BOLD, 14));

        btn.addActionListener(e -> {
            if (currentMode == Mode.REMOVE) {
                int res = JOptionPane.showConfirmDialog(this, "정말 '" + name + "' 버튼을 삭제할까요?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    buttonMap.remove(name);
                    buttonData.remove(name);
                    refreshButtons();
                    saveButtonsToFile();
                    statusTextArea.setText("'" + name + "' 버튼이 삭제되었습니다.");
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
