package Scripts;

import Cryptage.KeyGenCryptage;
import Cryptage.MessageCrypte;
import Cryptage.PublicKey;
import org.junit.After;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScriptTestScrutateur {

    public static void main(String[] args) throws IOException {
        ScriptTestScrutateur testRunner = new ScriptTestScrutateur();

        System.out.println("----------------------------------------------------");
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
        System.out.println("----------------------------------------------------");

        try{
            System.out.println("Test creation vote 1");
            testRunner.testRequestPublicKey();
            System.out.println("Test Request Public Key : Success");
        }
        catch (IOException e) {
            System.out.println("Test Request Public Key : Failure - " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------------------------------------");


        try{
            System.out.println("Test decrypt vote");
            testRunner.setUp();
            testRunner.testDecryptMessage();
            System.out.println("Test Decrypt Message : Success");
        }
        catch (IOException e) {
            System.out.println();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------------------------------------");


        try{
            System.out.println("Test creation vote 2");
            testRunner.setUp();
            testRunner.testRequestPublicKey2();
            System.out.println("Test Request Public Key 2 : Success");
        }
        catch (IOException e) {
            System.out.println("Test Request Public Key 2 : Failure - " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("----------------------------------------------------");
    }

    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private PublicKey publicKey;

    @BeforeEach
    public void setUp() throws IOException {
        // Se connecter au scrutateur
        clientSocket = new Socket("localhost", 5057);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ois = new ObjectInputStream(clientSocket.getInputStream());
    }

    @AfterEach
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

        publicKey = (PublicKey) ois.readObject();
        System.out.println("Public key received : ");
        System.out.println("G : " + publicKey.getG());
        System.out.println("H : "+ publicKey.getH());
        System.out.println("P : " + publicKey.getP());
        // Vérifier que la clé publique a été reçue
        Assertions.assertNotNull(publicKey);
    }

    @Test
    public void testRequestPublicKey2() throws IOException, ClassNotFoundException {
        // Demander la clé publique
        oos.writeUTF("newvote");
        oos.writeUTF("testvote2");
        oos.flush();

        publicKey = (PublicKey) ois.readObject();
        System.out.println("Public key received : ");
        System.out.println("G : " + publicKey.getG());
        System.out.println("H : "+ publicKey.getH());
        System.out.println("P : " + publicKey.getP());
        // Vérifier que la clé publique a été reçue
        Assertions.assertNotNull(publicKey);
    }

    @Test
    public void testDecryptMessage() throws IOException, ClassNotFoundException {
        System.out.println("Public key used : " + publicKey.getG());
        oos.writeUTF("decrypt");
        oos.writeUTF("testvote");
        MessageCrypte messageCrypte = new MessageCrypte(42,publicKey);
        oos.writeObject(messageCrypte);
        int messageDecrypte = (int) ois.readObject();
        System.out.println("Decoded message : " + messageDecrypte);
    }



}
