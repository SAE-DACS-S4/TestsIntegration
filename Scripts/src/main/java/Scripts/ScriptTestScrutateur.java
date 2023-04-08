package Scripts;

import Cryptage.KeyGenCryptage;
import Cryptage.PublicKey;
import org.junit.After;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.Assert.assertNotNull;

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
        try{
            testRunner.testRequestPublicKey();
            System.out.println("Test Request Public Key : Success");
        }
        catch (IOException e) {
            System.out.println("Test Request Public Key : Failure - " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    @BeforeAll
    public void setUp() throws IOException {
        // Se connecter au scrutateur
        clientSocket = new Socket("localhost", 5057);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ois = new ObjectInputStream(clientSocket.getInputStream());
    }

    @AfterAll
    public void tearDown() throws IOException {
        // Fermer les ressources
        ois.close();
        oos.close();
        clientSocket.close();
    }

    @Test
    public void testRequestPublicKey() throws IOException, ClassNotFoundException {
        // Demander la clé publique
        oos.writeUTF("newvote");
        oos.writeUTF("testvote");
        oos.flush();

        PublicKey publicKey = (PublicKey) ois.readObject();
        System.out.println("G : " + publicKey.getG());
        System.out.println("H : "+ publicKey.getH());
        System.out.println("P : " + publicKey.getP());



        // Vérifier que la clé publique a été reçue
        Assertions.assertNotNull(publicKey);
    }

}
