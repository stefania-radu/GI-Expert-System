package helper;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterHelper {

    private static String path = "conf/domain.txt";
    private static FileWriter domainFileWriter;

    public static void openFile() {
        try {
            domainFileWriter = new FileWriter(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToDomainFile(String variable, String value) {
        try {
            domainFileWriter.write(variable + ": " + value + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cleanDomainFile() throws IOException {
        domainFileWriter.close();
    }

}
