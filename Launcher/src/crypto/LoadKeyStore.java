package crypto;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Loader of the keyStore of the server
 * @author tbd
 * 
 */
public class LoadKeyStore {

    /**
     * Get the KeyPair of the keyStore of the server
     * @return The KeyPair of the the keyStore of the server
     */
    private static KeyPair getKeyPair() {
        FileInputStream is;
        KeyPair keypair = null;
        try {
            is = new FileInputStream("photoshare.store");

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, "server".toCharArray());

            String alias = "photoshare";

            Key keyStore = keystore.getKey(alias, "sc019!".toCharArray());
            if (keyStore instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);

                // Get public key
                PublicKey keypublic = cert.getPublicKey();

                // Return a key pair
                keypair = new KeyPair(keypublic, (PrivateKey) keyStore);
            }

            is.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return keypair;

    }

    /**
     * Get the public key of the keyStore of server
     * @return The public key of the keyStore of server
     */
    public static PublicKey getPublicKey() {
        return getKeyPair().getPublic();
    }

    /**
     * Get the private key of the keyStore of server
     * @return The private key of the keyStore of server
     */
    public static PrivateKey getPrivateKey() {
        return getKeyPair().getPrivate();
    }

}
