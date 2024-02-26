import org.example.AesEncryption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AesEncrytionTest {

    @Test
    public void testEncryption() throws Exception {
        AesEncryption aesEncryption = new AesEncryption();
        String message = "Hello, AES!";
        String secretKey = "MySecretKey";

        String encryptedMessage = aesEncryption.encrypt(message, secretKey);

        // Vérifie si le message chiffré est différent du message d'origine
        assertNotEquals(message, encryptedMessage);

        // Vérifie si le message déchiffré est identique au message d'origine
        assertEquals(message, aesEncryption.decrypt(encryptedMessage, secretKey));
    }

    @Test
    public void testDecryption() throws Exception {
        AesEncryption aesEncryption = new AesEncryption();
        String message = "Hello, AES!";
        String secretKey = "MySecretKey";

        String encryptedMessage = aesEncryption.encrypt(message, secretKey);

        // Vérifie si le message déchiffré est identique au message d'origine
        assertEquals(message, aesEncryption.decrypt(encryptedMessage, secretKey));
    }
}
