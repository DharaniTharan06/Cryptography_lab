import java.util.Random;

public class HellmanUtils {
    public static long modExp(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;

        while (exp > 0) {
            if ((exp & 1) == 1)
                result = (result * base) % mod;

            exp = exp >> 1;
            base = (base * base) % mod;
        }
        return result;
    }

    public static long generatePrivateKey() {
        Random rand = new Random();
        return rand.nextInt(100) + 1;
    }

    public static long generatePublicKey(long g, long privateKey, long p) {
        return modExp(g, privateKey, p);
    }

    public static long computeSharedSecret(long receivedPublic, long privateKey, long p) {
        return modExp(receivedPublic, privateKey, p);
    }

    public static String encrypt(String message, long key) {
        StringBuilder result = new StringBuilder();
        int shift = (int)(key % 26);

        for(char c : message.toCharArray()) {
            if(Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char)((c - base + shift) % 26 + base));
            } else result.append(c);
        }
        return result.toString();
    }

    public static String decrypt(String message, long key) {
        StringBuilder result = new StringBuilder();
        int shift = (int)(key % 26);

        for(char c : message.toCharArray()) {
            if(Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char)((c - base - shift + 26) % 26 + base));
            } else result.append(c);
        }
        return result.toString();
    }
}
