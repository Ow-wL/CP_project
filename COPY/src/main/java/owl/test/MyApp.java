package owl.test;

import javax.swing.*;
import java.awt.*;

public class MyApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("설정 패널 예제");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // 설정 패널 (툴바 느낌)
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        settingsPanel.add(new JLabel("설정:"));
        settingsPanel.add(new JButton("옵션 1"));
        settingsPanel.add(new JButton("옵션 2"));

        // 메인 콘텐츠
        JLabel mainContent = new JLabel("메인 화면입니다.", SwingConstants.CENTER);

        // 위쪽에 설정 패널, 가운데에 콘텐츠
        frame.add(settingsPanel, BorderLayout.NORTH);
        frame.add(mainContent, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}