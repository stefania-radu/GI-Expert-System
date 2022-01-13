package helper;

import model.Output;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OutputLoader {

    private static List<Output> outputs = new ArrayList<>();
    private static final int NR_OUTPUTS = 8;

    public static List<Output> loadOutputs() {
        if (outputs.isEmpty()) {
            try (InputStream inputStream = QuestionsLoader.class.getResourceAsStream("outputs.properties")) {

                Properties properties = new Properties();
                properties.load(inputStream);
                for (int i = 1; i <= NR_OUTPUTS; i++) {
                    Output output = new Output(i);
                    String variable = properties.getProperty("output" + i + ".variable");
                    output.setVariable(variable);
                    String title = properties.getProperty("output" + i + ".title");
                    output.setTitle(title);
                    String text = properties.getProperty("output" + i + ".text");
                    output.setText(text);
                    outputs.add(output);
                }
                return outputs;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return outputs;
        }

    }
}

