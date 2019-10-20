package editor;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * The type Password.
 */
class password {

    private static SecretKeySpec secretKeySpec = null;

    /**
     * Initialize.
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     */
    static void initialize() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // Das Passwort bzw der Schluesseltext
        String keyStr = "soundboard";
        // byte-Array erzeugen
        byte[] key = (keyStr).getBytes("UTF-8");
        // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        // nur die ersten 128 bit nutzen
        key = Arrays.copyOf(key, 16);
        // der fertige Schluessel
        secretKeySpec = new SecretKeySpec(key, "AES");
    }

    /**
     * Lock string.
     *
     * @param message the message
     * @return the string
     */
    static String lock(String message) {
        try {
            // Verschluesseln
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(message.getBytes());

            // bytes zu Base64-String konvertieren (dient der Lesbarkeit)
            BASE64Encoder myEncoder = new BASE64Encoder();
            return myEncoder.encode(encrypted);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Entlock string.
     *
     * @param input the input
     * @return the string
     */
    static String entlock(String input) {
        try {

            // BASE64 String zu Byte-Array konvertieren
            BASE64Decoder myDecoder2 = new BASE64Decoder();
            byte[] crypted2 = myDecoder2.decodeBuffer(input);

            // Entschluesseln
            Cipher cipher2 = Cipher.getInstance("AES");
            cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] cipherData2 = cipher2.doFinal(crypted2);
            return new String(cipherData2);
        } catch (Exception ignored) {
        }
        return null;
    }
}