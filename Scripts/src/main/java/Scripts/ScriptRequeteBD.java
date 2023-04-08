package Scripts;


import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class ScriptRequeteBD {
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        ScriptRequeteBD testRunner = new ScriptRequeteBD();
        System.out.println("----------------------------------------------------");
        try {
            testRunner.init();
        } catch (SQLNonTransientConnectionException e) {
            System.out.println("Erreur de connexion à la base de données, veuillez vérifier que le docker est bien lancé");
            System.exit(1);
        }
        try {
            testRunner.testConnexion();
            System.out.println("\nTest Connexion: Success");
        } catch (AssertionError e) {
            System.out.println("Test Connexion: Failure - " + e.getMessage());
        }
        System.out.println("----------------------------------------------------");

        try {
            testRunner.testInsertUtilisateur();
            System.out.println("\nTest Insert Utilisateur: Success");
        } catch (AssertionError e) {
            System.out.println("Test Insert Utilisateur: Failure - " + e.getMessage());
        }

        System.out.println("----------------------------------------------------");

        try {
            testRunner.testSelect();
            System.out.println("\nTest Select: Success");
        } catch (AssertionError e) {
            System.out.println("Test Select: Failure - " + e.getMessage());
        }
        System.out.println("----------------------------------------------------");

        try {
            testRunner.supprimerUtilisateur();
            System.out.println("\nTest Suppression Utilisateur: Success");
        } catch (AssertionError e) {
            System.out.println("Test Suppression Utilisateur: Failure - " + e.getMessage());
        }

        System.out.println("----------------------------------------------------");

        try {
            testRunner.testSelect();
            System.out.println("\nTest Select : Success");
        } catch (AssertionError e) {
            System.out.println("Test Select 2: Failure - " + e.getMessage());
        }


        System.out.println("----------------------------------------------------");

        try {
            testRunner.testSelect2();
            System.out.println("\nTest Select 2: Success");
        } catch (AssertionError e) {
            System.out.println("Test Select 2: Failure - " + e.getMessage());
        }
        System.out.println("----------------------------------------------------");
        cleanup();
    }


    @BeforeAll
    public static void init() throws SQLException {
        System.out.println("Tentative de connexion à la base de données");
        String url = "jdbc:mariadb://localhost:3306/evote";
        String user = "evote";
        String password = "123";
        conn = DriverManager.getConnection(url, user, password);
        if (conn.isValid(5)) {
            System.out.println("\nURL de la BD : " + conn);
        }
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testConnexion() throws SQLException {
        assertTrue(conn.isValid(5));
    }

    @Test
    public void testInsertUtilisateur() throws SQLException {
        System.out.println("Tentative d'insertion d'un utilisateur\n");

        String login = "johndoe";
        String password = "password123";
        int vote = 0;
        String mail = "johndoe@example.com";
        int essaiRestant = 3;
        String currentTimeStamp = null;
        String insertSql = String.format("INSERT INTO utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp) VALUES ('%s', '%s', %d, '%s', %d, %s)",
                login, password, vote, mail, essaiRestant, currentTimeStamp);
        Statement statement = conn.createStatement();

        String selectSql = String.format("SELECT * FROM utilisateur WHERE login = '%s'", login);
        ResultSet resultSet = statement.executeQuery(selectSql);
        if (!resultSet.next()) {
            System.out.println("Insertion de l'utilisateur : " + login);
            statement.execute(insertSql);
        } else {
            System.out.println("Utilisateur existant : " + login);
        }
        resultSet.close();
        statement.close();
    }

    @Test
    public void testSelect() throws SQLException {
        System.out.println("Tentative de select tous les utilisateurs\n");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM utilisateur");

        assertTrue(rs.next());
        System.out.println("Voici les utilisateurs de la base de données :");
        while (rs.next()) {
            System.out.println("login : " + rs.getString("login") + " mail : " + rs.getString("mail"));

        }
        stmt.close();
    }

    @Test
    public void supprimerUtilisateur() throws SQLException {
        System.out.println("Tentative de suppression d'un utilisateur\n");
        String login = "johndoe";
        String deleteSQL = "DELETE FROM utilisateur WHERE login = 'johndoe'";
        Statement statement = conn.createStatement();
        int affectedRows = statement.executeUpdate(deleteSQL);

        if (affectedRows > 0) {
            System.out.println("Utilisateur : " + login + " supprimé !");
        } else {
            System.out.println("Utilisateur non supprimé");
        }
        statement.close();
    }


    @Test
    public void testSelect2() throws SQLException {
        System.out.println("Tentative de select tous les utilisateurs qui ont une adresse mail\n");
        String url = "jdbc:mariadb://localhost:3306/evote";
        String user = "evote";
        String password = "123";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            String selectQuery = "SELECT * FROM utilisateur WHERE mail IS NOT NULL";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                String login = resultSet.getString("login");
                String mail = resultSet.getString("mail");
                System.out.println("login: " + login + ", mail: " + mail);
                assertTrue(mail.contains("@"));
                assertNotEquals("admin", login);
                assertNotEquals("axelf", login);
                assertNotEquals("quentinb", login);
            }
            statement.close();
        }
    }
}
