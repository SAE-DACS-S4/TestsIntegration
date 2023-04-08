package Scripts;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;


public class ScriptDockerComposeBD {
    /**
     * Ce script permet de tester le fichier docker-compose de la base de données et d'exécuter le script SQL
     */
    public static void main(String[] args) {
        try {
            // Charger le fichier docker-compose.yml depuis les ressources du classpath
            InputStream inputStream = ScriptDockerComposeBD.class.getResourceAsStream("/docker-compose.yml");
            if (inputStream == null) {
                throw new RuntimeException("docker-compose.yml not found in classpath resources");
            }
            // Créer un fichier temporaire pour stocker le docker-compose.yml extrait du JAR
            File tempFile = File.createTempFile("docker-compose", ".yml");
            tempFile.deleteOnExit();
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Exécuter Docker Compose en utilisant le fichier temporaire et en mode détaché
            ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "-f", tempFile.getAbsolutePath(), "up", "-d");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();


            // Attendre 10 secondes pour que la base de données soit prête
            System.out.println("En attente de 10 secondes pour que la base de données soit prête...");
            Thread.sleep(10000);

            // Test de la connexion à la base de données
            String url = "jdbc:mariadb://localhost:3306/evote";
            String user = "evote";
            String password = "123";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Connexion à la base de données réussie !");

                // Charger le script SQL depuis les ressources du classpath
                InputStream sqlInputStream = ScriptDockerComposeBD.class.getClassLoader().getResourceAsStream("sql/DB_EVOTE_SCRIPT.sql");

                if (sqlInputStream == null) {
                    throw new RuntimeException("DB_EVOTE_SCRIPT.sql not found in classpath resources");
                }

                // Exécuter le script SQL
                String sql = new BufferedReader(new InputStreamReader(sqlInputStream)).lines().collect(Collectors.joining("\n"));


                // Exécuter le script SQL
                Statement statement = connection.createStatement();

                // Divisez la chaîne SQL en une liste d'instructions
                String[] sqlStatements = sql.split(";\\s*");

                // Exécutez chaque instruction individuellement
                for (String sqlStatement : sqlStatements) {
                    if (!sqlStatement.trim().isEmpty()) {
                        statement.execute(sqlStatement);
                    }
                }

                System.out.println("Script SQL exécuté avec succès !");


            } catch (SQLException e) {
                System.err.println("Échec de la connexion à la base de données ou de l'exécution du script SQL.");
                e.printStackTrace();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
