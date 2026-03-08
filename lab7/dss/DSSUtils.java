import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class DSSUtils {

    public static final BigInteger P = new BigInteger("23");
    public static final BigInteger Q = new BigInteger("11");
    public static final BigInteger G = new BigInteger("4");

    public static BigInteger[] generateKeys() {

        SecureRandom random = new SecureRandom();

        BigInteger x;

        do {
            x = new BigInteger(Q.bitLength(), random).mod(Q);
        } while (x.compareTo(BigInteger.ZERO) <= 0);

        BigInteger y = G.modPow(x, P);

        return new BigInteger[]{x, y};
    }

    public static BigInteger hash(String message) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] digest = md.digest(message.getBytes("UTF-8"));

        return new BigInteger(1, digest);
    }

    public static BigInteger[] sign(String message, BigInteger privateKey) throws Exception {

        SecureRandom random = new SecureRandom();

        BigInteger r, s, k, kInverse, hashVal;

        hashVal = hash(message).mod(Q);

        do {

            do {
                k = new BigInteger(Q.bitLength(), random).mod(Q);
            } while (k.compareTo(BigInteger.ZERO) <= 0);

            r = G.modPow(k, P).mod(Q);

            kInverse = k.modInverse(Q);

            s = kInverse.multiply(hashVal.add(privateKey.multiply(r))).mod(Q);

        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));

        return new BigInteger[]{r, s};
    }

    public static boolean verify(String message, BigInteger r,
                                 BigInteger s, BigInteger publicKey) throws Exception {

        if (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(Q) >= 0)
            return false;

        if (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(Q) >= 0)
            return false;

        BigInteger hashVal = hash(message).mod(Q);

        BigInteger w = s.modInverse(Q);

        BigInteger u1 = hashVal.multiply(w).mod(Q);

        BigInteger u2 = r.multiply(w).mod(Q);

        BigInteger v = G.modPow(u1, P)
                .multiply(publicKey.modPow(u2, P))
                .mod(P)
                .mod(Q);

        return v.equals(r);
    }
}