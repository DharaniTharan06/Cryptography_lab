public class HomoRsa {

    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static int[] extendedEuclidean(int a, int b) {
        if (b == 0)
            return new int[]{a, 1, 0};

        int[] vals = extendedEuclidean(b, a % b);
        int d = vals[0];
        int x = vals[2];
        int y = vals[1] - (a / b) * vals[2];

        return new int[]{d, x, y};
    }

    public static int modInverse(int e, int phi) {
        int[] vals = extendedEuclidean(e, phi);
        int gcd = vals[0];
        int x = vals[1];

        if (gcd != 1)
            throw new ArithmeticException("Inverse does not exist.");

        return (x % phi + phi) % phi;
    }

    public static long modExp(long base, long exponent, long modulus) {
        long result = 1;
        base = base % modulus;

        while (exponent > 0) {
            if ((exponent & 1) == 1)
                result = (result * base) % modulus;

            base = (base * base) % modulus;
            exponent >>= 1;
        }
        return result;
    }

    public static long encrypt(long message, long e, long n) {
        return modExp(message, e, n);
    }

    public static long decrypt(long cipher, long d, long n) {
        return modExp(cipher, d, n);
    }
}