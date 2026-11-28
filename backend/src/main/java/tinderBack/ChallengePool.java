package tinderBack;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;

public class ChallengePool {
    private static HashMap<String, Long> pool;
    private static final long OFFSET_MILLIS = 60 * 60 * 1000;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private void cleanup() {
        for (Entry<String, Long> entry : pool.entrySet()) {
            if (entry.getValue() < System.currentTimeMillis()) {
                pool.remove(entry.getKey());
            }
        }
    }

    public String getChallenge() {
        cleanup();
        String challenge = newChallenge(32);
        pool.put(challenge, System.currentTimeMillis() + OFFSET_MILLIS);
        return challenge;
    }

    public boolean checkChallenge(String chlg) {
        cleanup();
        return pool.containsKey(chlg);
    }

    private String newChallenge(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_URL_ENCODER.encodeToString(randomBytes);
    }
}
