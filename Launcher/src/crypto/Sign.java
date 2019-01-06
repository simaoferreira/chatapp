package crypto;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Handler the signature of the files
 * @author tbd
 *
 */
public class Sign {


    protected static ArrayList<String> allFileNames;

    /**
     * Signs a file (after encrypting it)
     * @param filename - file to be signed
     * @throws Exception
     */
    public static void signFile(String filename) throws Exception {
        String filenameExport = filename + ".sig";
        FileWriter  fl = new FileWriter(filenameExport);
        PrintWriter pw = new PrintWriter(fl);

        // Read bytes of file to encrypt
        byte[] array = Files.readAllBytes(new File(filename).toPath());

        Signature sig = Signature.getInstance("SHA256WithRSA");
        sig.initSign(LoadKeyStore.getPrivateKey());
        sig.update(array);

        byte[] signatureBytes = sig.sign();

        // Encrypts using AES 
        pw.write(new String(Base64.getEncoder().encode(signatureBytes)));
        pw.close();
        fl.close();
    }

    /**
     * Verifies if a file is valid by decrypting with public key
     * @param filename - file to be verified (does not include .sig, it is the raw file)
     * @return true if is valid, false if otherwise
     * @throws Exception
     */
    public static boolean verifyFile(String filename) throws Exception {

        String filenameSignature = filename + ".sig";

        //Read bytes of file signature
        byte[] signatureBytes = Base64.getDecoder().decode(Files.readAllBytes(new File(filenameSignature).toPath()));

        //Read bytes of file
        byte[] data = Files.readAllBytes(new File(filename).toPath());

        Signature sig = Signature.getInstance("SHA256WithRSA");
        sig.initVerify(LoadKeyStore.getPublicKey());
        sig.update(data);

        if(sig.verify(signatureBytes)) {
            return true;            
        }else{
            System.out.println("Could not verify  file " + filename +", might be corrupted!");
  
            if(Decrypt.decryptedFileExists(filename))
                Decrypt.deleteDecryptedFie(filename);
            
            return false;            
        }
    }
    
}
