package owl.copy;

import java.io.*;

public class Config {
    private static final String FOLDER_PATH = System.getenv("APPDATA") + "\\Copy_OwL";
    private static final String SETTINGS_FILE = FOLDER_PATH + "\\settings.txt";

    // 새로운 버튼 데이터 파일 경로를 설정 파일에 저장하는 메소드
    public static String getButtonJsonPath() {
        try {
            // Copy_OwL 폴더가 존재하지 않으면 생성
            File folder = new File(FOLDER_PATH);
            if (!folder.exists()) {
                folder.mkdirs(); // 폴더 생성
            }

            // settings.txt 파일이 존재하면 경로를 읽어옵니다.
            File settingsFile = new File(SETTINGS_FILE);
            if (settingsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
                String path = reader.readLine();
                reader.close();
                return path;
            } else {
                // 설정 파일이 없으면 기본 경로를 반환합니다.
                return FOLDER_PATH + "\\buttons.json";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 새로운 버튼 데이터 파일 경로를 설정 파일에 저장하는 메소드
    public static void setButtonJsonPath(String newPath) {
        try {
            // Copy_OwL 폴더가 존재하지 않으면 생성
            File folder = new File(FOLDER_PATH);
            if (!folder.exists()) {
                folder.mkdirs(); // 폴더 생성
            }

            // 새로운 경로를 settings.txt에 저장합니다.
            File settingsFile = new File(SETTINGS_FILE);
            if (!settingsFile.exists()) {
                settingsFile.createNewFile(); // 파일 생성
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
            writer.write(newPath); // 새로운 경로를 파일에 기록
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
