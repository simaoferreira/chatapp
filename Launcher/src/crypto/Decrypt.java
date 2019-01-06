package crypto;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * Handler the decryption
 * @author tbd
 *
 */
public class Decrypt {

    /* Prefix of decrypted files */
    protected static final String PREFIX_DECRYPTED = "DEC";
    protected static final String USERS_FILE       = "users.txt";
    protected static ArrayList<String> allFileNames;

    /**
     * Decipher a file
     * @param filename The name of the file to be deciphered
     * @return true if the file was deciphered successfully otherwise false
     */
    public static boolean decryptFile(String filename) {

        String filenameWithDEC = returnFilenameWithPathDec(filename);
        try {
            decryptRandomKeyWithPrivateKey(filename, filenameWithDEC);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Decrypts an encrypted random key with the private key of server
     * @param filenameImport - file to be encrypted
     * @param filenameExport - filename after file encryption
     * @return true if successfully encrypted, false if otherwise
     * @throws Exception
     */
    private static boolean decryptRandomKeyWithPrivateKey(String filenameImport, String filenameExport) throws Exception {
        try {
            byte[] array = Base64.getDecoder().decode(Files.readAllBytes(new File(filenameImport + ".key").toPath()));
            SecretKey randomKey = unwrapRandomKey(array);

            decryptFileWithRandomKey(filenameImport, filenameExport, randomKey);
            return true;
        } catch(Exception e) {
            System.out.println("Could not decrypt file " + filenameImport +", might be corrupted!");
            return false;
        }
    }

    /**
     * Decrypts an encrypted file with symmetric key
     * @param filenameImport - file to be encrypted
     * @param filenameExport - filename after file encryption
     * @param randomKey - symmetric key
     * @return true if successfully decrypted, false if otherwise
     * @throws Exception
     */
    private static void decryptFileWithRandomKey(String filenameImport, String filenameExport, SecretKey randomKey) throws Exception {

        FileOutputStream      fl = new FileOutputStream(filenameExport);
        BufferedOutputStream bos = new BufferedOutputStream(fl);

        /* Read encrypted key from file */ 
        try {
            byte[] array = Base64.getDecoder().decode(Files.readAllBytes(new File(filenameImport).toPath()));
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, randomKey);

            /* File bytes after decrypted */
            byte[] decryptedBytes = cipher.doFinal(array);
            bos.write(decryptedBytes, 0 , decryptedBytes.length);
            bos.flush();
            bos.close();
            fl.close();
        } catch (Exception e) {
            bos.close();
            fl.close();
            System.out.println("Could not decrypt file " + filenameImport +", might be corrupted!");

            deleteDecryptedFie(filenameImport);
            
        }
    }

    /**
     * Deletes a deciphered file
     * @param filename - file to delete
     */
    public static void deleteDecryptedFie(String filename) {
        File f = new File(returnFilenameWithPathDec(filename));
        if(f.exists())
            f.delete();
    }

    /**
     * Checks if a file is deciphered and signed
     * @param filename - file to delete (contains the name of the file with extension and without prefix)
     * @return true if there is a file deciphered, false if otherwise
     */
    public static boolean isFileDecrypted(String filename) {

        /* Before deciphering, check the authenticity of the file using the signature */
        String filenameWithDEC = returnFilenameWithPathDec(filename);

        File f = new File(filenameWithDEC);
        if( f.exists()) {
            try {
                return Sign.verifyFile(filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks if a file is deciphered
     * @param filename - file to delete (contains the name of the file with extension and without prefix)
     * @return true if there is a file deciphered and valid, false if otherwise
     */
    public static boolean decryptedFileExists(String filename) {
        return new File(returnFilenameWithPathDec(filename)).exists();
    }

    /**
     * Unwraps the symmetric key with private key
     * @param wrappedKey - the wrapped key
     * @return the symmetric key
     * @throws Exception
     */
    private static SecretKey unwrapRandomKey(byte[] wrappedKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.UNWRAP_MODE, LoadKeyStore.getPrivateKey());
        return (SecretKey) cipher.unwrap( wrappedKey, "AES", Cipher.SECRET_KEY);
    }

    /**
     * Creates the file path of deciphered file to be exported
     * @param filenamePath
     * @return the file path
     */
    public static String returnFilenameWithPathDec(String filenamePath) {
        String filename;
        if(filenamePath.contains("/")) {
            String[] path = filenamePath.split("/");
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < path.length; i++) {
                if(i == path.length-1) {
                    sb.append(PREFIX_DECRYPTED + path[i]);
                }else {
                    sb.append(path[i] + "/");
                }
            }
            filename = sb.toString();
            
        }else{
            filename = PREFIX_DECRYPTED + filenamePath;
        }
        
        return filename;
    }

}
