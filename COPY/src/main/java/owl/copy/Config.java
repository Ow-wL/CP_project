package owl.copy;

import java.io.*;

public class Config {
    private static final String APP_FOLDER = "Copy_OwL";
    private static final String SETTINGS_FILE_NAME = "settings.txt";

    private static String getSettingsFilePath() {
        String appData = System.getenv("APPDATA");
        if (appData == null || appData.isEmpty()) {
            // APPDATA 환경 변수가 없을 경우, 기본 사용자 홈 디렉토리를 사용하도록 예외 처리
            appData = System.getProperty("user.home");
            if (appData != null) {
                return appData + File.separator + "AppData" + File.separator + "Roaming" + File.separator + APP_FOLDER + File.separator + SETTINGS_FILE_NAME;
            } else {
                // 정말로 홈 디렉토리도 찾을 수 없는 최악의 경우
                return APP_FOLDER + File.separator + SETTINGS_FILE_NAME;
            }
        }
        return appData + File.separator + APP_FOLDER + File.separator + SETTINGS_FILE_NAME;
    }

    // Copy_OwL 폴더 경로를 동적으로 생성
    private static String getAppFolderPath() {
        String appData = System.getenv("APPDATA");
        if (appData == null || appData.isEmpty()) {
            appData = System.getProperty("user.home");
            if (appData != null) {
                return appData + File.separator + "AppData" + File.separator + "Roaming" + File.separator + APP_FOLDER;
            } else {
                return APP_FOLDER;
            }
        }
        return appData + File.separator + APP_FOLDER;
    }

    // 환경 변수를 확장하고 "buttons.json"을 결합한 최종 경로를 반환
    private static String expandPath(String storedPath) {
        if (storedPath != null && storedPath.contains("%APPDATA%")) {
            return storedPath.replace("%APPDATA%", System.getenv("APPDATA"));
        }
        return storedPath;
    }

    // 새로운 버튼 데이터 파일 경로를 설정 파일에 저장하는 메소드
    public static String getButtonJsonPath() {
        String appFolderPath = getAppFolderPath();
        File folder = new File(appFolderPath);
        if (!folder.exists()) {
            folder.mkdirs(); // 폴더 생성
        }

        File settingsFile = new File(getSettingsFilePath());
        if (settingsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
                String storedPath = reader.readLine();
                String expandedPath = expandPath(storedPath);
                if (expandedPath != null && !expandedPath.trim().isEmpty()) {
                    return expandedPath;
                } else {
                    return appFolderPath + File.separator + "buttons.json"; // 설정 파일에 유효한 경로가 없으면 기본 경로 반환
                }
            } catch (IOException e) {
                e.printStackTrace();
                return appFolderPath + File.separator + "buttons.json"; // 파일 읽기 오류 시 기본 경로 반환
            }
        } else {
            return appFolderPath + File.separator + "buttons.json"; // 설정 파일이 없으면 기본 경로 반환
        }
    }

    // 새로운 버튼 데이터 파일 경로를 설정 파일에 저장하는 메소드
    public static void setButtonJsonPath(String newPath) {
        String appFolderPath = getAppFolderPath();
        File folder = new File(appFolderPath);
        if (!folder.exists()) {
            folder.mkdirs(); // 폴더 생성
        }

        File settingsFile = new File(getSettingsFilePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile))) {
            writer.write(newPath); // 새로운 경로를 파일에 기록 (환경 변수 형태 그대로 저장)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
