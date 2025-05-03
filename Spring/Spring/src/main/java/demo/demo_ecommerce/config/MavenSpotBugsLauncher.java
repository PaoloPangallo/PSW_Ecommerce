package demo.demo_ecommerce.config;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MavenSpotBugsLauncher {
    public static void main(String[] args) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:\\Users\\paolo\\Desktop\\apache-maven-3.8.8\\bin\\mvn.cmd",
                    "spotbugs:gui",
                    "-Dspotbugs.failOnError=false"  // opzionale, per evitare che la build fallisca se vengono trovati bug
            );
            // Imposta la directory di lavoro (dove è presente il file pom.xml)
            processBuilder.directory(new File("C:\\Users\\paolo\\Desktop\\PSW_NUOVO\\Spring\\Spring"));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("SpotBugs terminato con codice: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
