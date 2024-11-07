package email;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptionTest {
    private final Encryption encryption = createEncryption();

    @Test
    void encryptString() throws Exception {
        String encryptedText = encryption.encrypt("Unlock Your Potential with the Advent Of Craft Calendar!");
        assertThat(encryptedText)
                .isEqualTo("L7wht/YddOoTvYvrc+wFcZhtXNvZ2cHFxq9ND27h1Ovv/aWLxN8lWv1xMsguM/R4Yodk3rn9cppI+YarggtPjA==");
    }


    private static Encryption createEncryption() {
        try {
            return new Encryption(
                    new Configuration(convertKey("Advent Of Craft"), convertIv("2024"))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertKey(String key) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha256.digest(key.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    private static String convertIv(String iv) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] ivBytes = md5.digest(iv.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(ivBytes);
    }
}
