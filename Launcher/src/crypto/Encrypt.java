package crypto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Handler the encryption
 * @author tbd
 *
 */
public class Encrypt {

    private static PublicKey publicKey;
    protected static final String HASHES_FILE = "hashedUsers.txt";
    protected static ArrayList<String> allFileNames;

    /**
     * Encrypts a file upon its creation
     * @param filenameImport - File to cipher
     * @return true if file was successfully encrypted, false if otherwise
     */
    public static boolean cipherFile(String filenameImport) {
        return beginEncryptionFile(filenameImport, filenameImport);
    }

    /**
     * Encrypts a file that has been previously deciphered
     * @param filenameImport - File to cipher
     * @return true if file was successfully encrypted, false if otherwise
     */
    public static boolean cipherDecryptedFile(String filenameImport) {
        String filenameExport = filenameImport;
        String filenameWithDEC = Decrypt.returnFilenameWithPathDec(filenameImport);
        return beginEncryptionFile(filenameWithDEC, filenameExport);
    }

    /**
     * Recalculate hash of a file (checksum) and appends it to the hashes file
     * @param adminPassword  admin password used to create MAC
     */
    public static void recalculateHash(String adminPassword) {
        //manUsers.writeHashesToFile(HASHES_FILE, adminPassword);
    }

    /**
     * Starts the encryption of a file
     * @param filenameImport - file to be encrypted
     * @param filenameExport - filename after file encryption
     * @return true if file was successfully encrypted, false if otherwise
     */
    public static boolean beginEncryptionFile(String filenameImport, String filenameExport) {

        try {
            //Ponto I.   <O servidor gera uma chave simétrica K AES aleatoriamente>
            SecretKey randomkey = createSymKey();

            //Ponto II.  <Cifra o conteúdo do ficheiro F recebido do cliente com K usando AES>
            encryptFileWithRandomKey(filenameImport, filenameExport, randomkey);
            //System.out.println("Encrypted File with random key");

            //Ponto III. <O servidor cifra K usando a sua chave pública, e armazena-a num ficheiro separado com extensão .key>
            encryptRandomKeyWithPublicKey(filenameExport, randomkey);
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //Ponto I.
    /**
     * Create symetric key
     * @return the symetric key
     * @throws Exception
     */
    private static SecretKey createSymKey() throws Exception {
        //gerar uma chave aleatória para utilizar com o AES
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        return kg.generateKey();
    }

    //Ponto II.
    /**
     * Encrypts a file given a symetric key
     * @param fileNameImport - file to be encrypted
     * @param fileNameExport - filename after file encryption
     * @param randomKey - symetric key
     * @throws Exception
     */
    private static void encryptFileWithRandomKey(String fileNameImport, String fileNameExport, SecretKey randomKey) throws Exception {
        // Read bytes of file to encrypt
        byte[] array = Files.readAllBytes(new File(fileNameImport).toPath());

        // Delete that file
        deleteOriginalFile(fileNameImport);

        FileWriter     fl = new FileWriter(fileNameExport);
        BufferedWriter pw = new BufferedWriter(fl);

        // Encrypts using AES 
        pw.write(new String(Base64.getEncoder().encode((encrypt(array, randomKey)))));

        pw.close();
        fl.close();

        // After encrypting create signature file
        Sign.signFile(fileNameExport);

    }

    //Ponto III.
    /**
     * Encrypts symetric key with public key
     * @param filenameImport - file to be encrypted
     * @param randomKey - symetric key
     * @throws Exception
     */
    private static void encryptRandomKeyWithPublicKey(String filenameImport, SecretKey randomKey) throws Exception {
        publicKey = LoadKeyStore.getPublicKey();

        String filenameExport = filenameImport + ".key";

        FileWriter  fl = new FileWriter(filenameExport);
        PrintWriter pw = new PrintWriter(fl);

        //Encrypts bytes with public key
        byte[] keyEncoded = encryptRandomKey(publicKey, randomKey);

        //Converts to base64 and writes to file
        pw.write(new String(Base64.getEncoder().encode(keyEncoded)) );
        pw.close();
        fl.close();

    }

    /**
     * Encrypt the string with the public key
     * @param publicKey - Public key of server
     * @param message - message to encrypt
     * @return bytes after encrypting
     * @throws Exception
     */
    public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        return cipher.doFinal(message.getBytes());  
    }

    /**
     * Encrypt symmetric key with public key
     * @param publicKey - Public key of server
     * @param randomKey - symmetric key
     * @return bytes after encrypting
     * @throws Exception
     */
    public static byte[] encryptRandomKey(PublicKey publicKey, SecretKey randomKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.WRAP_MODE, publicKey);
        return cipher.wrap(randomKey);
    }

    /**
     * Encrypt bytes with AES algorithm.
     *
     * @param data The bytes to encrypt
     * @param key The secret key to use in Cipher
     * @return the encrypted string
     */
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(data);
    }

    /**
     * Delete a file
     * @param filename The name of the file to be deleted
     */
    public static void deleteOriginalFile(String filename) {
        File f = new File(filename);
        if(f.exists())
            f.delete();
    }

}
