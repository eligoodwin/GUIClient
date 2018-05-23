//Source: https://www.mkyong.com/java/java-asymmetric-cryptography-example/
package Cryptography;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class LocalAsymmetricCrypto {
    private static final String KEYS_FOLDER = "Keys";
    private static final String PRIVATE_FILE = "Private";
    private static final String PUBLIC_FILE = "Public";
    private String private_path = "./Keys/Private";
    private String public_path  = "./Keys/Public";
    private static final String ENCRYPTION_METHOD = "RSA";
    private static final int KEY_LENGTH = 1024;
    private Cipher cipher;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    public PublicKey getPublicKey(){
        return publicKey;
    }
    public LocalAsymmetricCrypto() throws Exception {
        //generate local paths
        private_path = "." + File.separator + KEYS_FOLDER + File.separator + PRIVATE_FILE;
        public_path = "." + File.separator + KEYS_FOLDER + File.separator + PUBLIC_FILE;
        //TODO: if we have stored messages, we will have to store
        //  keys more securely and permanently or use the api key
        //  for encrypting the stored messages
        if (!getKeys()){
            generateKeys();
        }
        this.cipher = Cipher.getInstance(ENCRYPTION_METHOD);
    }

    //Returns true if keys can be retrieved from expected files
    //Returns false if keys or files are incorrect or don't exist
    private boolean getKeys(){
        File publicKeyFile = new File(private_path);
        File privateKeyFile = new File(public_path);
        //if key files exist
        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            //read keys
            try{
                //these throw exceptions if could not read or keys incorrect length
                privateKey = readPrivate();
                publicKey = readPublic();
                System.out.println("Trace in getKeys: keys found");
                return true;
            }
            catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        //one or more files don't exist
        else return false;
    }

    private void generateKeys() throws Exception {
        System.out.println("Trace in generateKeys: generating keys");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ENCRYPTION_METHOD);
        keyGen.initialize(KEY_LENGTH);
        pair = keyGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        storeKey(private_path, privateKey.getEncoded());
        storeKey(public_path, publicKey.getEncoded());
    }

    private void storeKey(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        System.out.println("Trace storeKey. Store to: " + path + ". Storing: ");
        System.out.println(key);

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    private PrivateKey readPrivate() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(private_path).toPath());
        System.out.println("Bytes read in private: " + keyBytes.length);
        //TODO: the below is wrong - how to check tampering?
        //if (keyBytes.length != KEY_LENGTH) throw new IllegalArgumentException();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ENCRYPTION_METHOD);
        return kf.generatePrivate(spec);
    }

    private PublicKey readPublic() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(public_path).toPath());
        System.out.println("Bytes read in public: " + keyBytes.length);
        //TODO: the below is wrong - how to check tampering?
        //if (keyBytes.length != KEY_LENGTH) throw new IllegalArgumentException();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(ENCRYPTION_METHOD);
        return kf.generatePublic(spec);
    }

    public String encryptString(String msg, PublicKey key) throws Exception {
        PublicKey useKey = key;
        if(useKey == null) useKey = publicKey;
        cipher.init(Cipher.ENCRYPT_MODE, useKey);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
    }

    public String decryptString(String msg, PrivateKey key)throws Exception {
        PrivateKey useKey = key;
        if(useKey == null) useKey = privateKey;
        cipher.init(Cipher.DECRYPT_MODE, useKey);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
    }
}
