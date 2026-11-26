package tinderBack;

import java.util.HashMap;
import java.util.Map.Entry;

public class ChallengePool {
    private HashMap<String, Long> pool;
    private long OFFSET_MILLIS = 60 * 60 * 1000;

    private void cleanup() {
        for (Entry<String, Long> entry : pool.entrySet()) {
            if (entry.getValue() < System.currentTimeMillis()) {
                pool.remove(entry.getKey());
            }
        }
    }

    public String getChallenge() {
        cleanup();
        String challenge = "generate random pls";
        pool.put(challenge, System.currentTimeMillis() + OFFSET_MILLIS);
        return challenge;
    }

    public boolean checkChallenge(String chlg) {
        cleanup();
        return pool.containsKey(chlg);
    }

    private String newChallenge() {return "";}
}
