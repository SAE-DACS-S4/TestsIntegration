package Scripts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

public class ScriptTestScrutateur {

    public static void main(String[] args) {
        ScriptTestScrutateur testRunner = new ScriptTestScrutateur();

        try {
            testRunner.setUp();
            System.out.println("Test Connexion to scrutator : Success");
        }
        catch (ConnectException e){
            System.out.println("Test connexion to scrutator : Failure - " + e.getMessage());
            System.out.println("Verify the scrutator is running");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    @BeforeEach
    public void setUp() throws IOException {
        // Se connecter au scrutateur
        clientSocket = new Socket("localhost", 5057);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ois = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Test
    public void testConnexion() throws IOException {

    }

}
