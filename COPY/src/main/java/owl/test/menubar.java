package owl.test;

import javax.swing.*;

public class menubar {

    public static void main(String[] args) {
        JFrame frame = new JFrame("My Swing App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // 메뉴바 생성
        JMenuBar menuBar = new JMenuBar();

        // "설정" 메뉴 추가
        JMenu settingsMenu = new JMenu("설정");
        JMenuItem option1 = new JMenuItem("옵션 1");
        JMenuItem option2 = new JMenuItem("옵션 2");
        settingsMenu.add(option1);
        settingsMenu.add(option2);

        // 메뉴바에 메뉴 추가
        menuBar.add(settingsMenu);

        // 프레임에 메뉴바 설정
        frame.setJMenuBar(menuBar);

        // 기본 콘텐츠
        JLabel label = new JLabel("메인 화면입니다.", SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);
    }
}
