
import java.util.*;

public class CipherUtils {

    public static String caesar(String text, int key, int toggle) {
        StringBuilder result = new StringBuilder();
        if (toggle == 1) {
            for (char character : text.toCharArray()) {
                if (Character.isLetter(character)) {
                    char base = Character.isUpperCase(character) ? 'A' : 'a';
                    result.append((char) (((character - base + key) % 26) + base));
                } else {
                    result.append(character);
                }
            }
            return result.toString();
        } else {
            for (char character : text.toCharArray()) {
                if (Character.isLetter(character)) {
                    char base = Character.isUpperCase(character) ? 'A' : 'a';
                    result.append((char) (((character - base - key + 26) % 26) + base));
                } else {
                    result.append(character);
                }
            }
            return result.toString();
        }
    }

    public static String monoalphabetic(String text, String key, int toogle) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        key = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        if (toogle == 1) {
            for (char c : text.toUpperCase().toCharArray()) {
                if (Character.isLetter(c)) {
                    int index = alphabet.indexOf(c);
                    result.append(key.charAt(index));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        } else {
            for (char c : text.toUpperCase().toCharArray()) {
                if (Character.isLetter(c)) {
                    int index = key.indexOf(c);
                    result.append(alphabet.charAt(index));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }
    }

    public static String playfair(String text, String key, int toogle) {
        if (toogle == 1) {
            char[][] matrix = generatePlayfairMatrix(key);
            text = text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
            StringBuilder sb = new StringBuilder(text);
            for (int i = 0; i < sb.length() - 1; i += 2) {
                if (sb.charAt(i) == sb.charAt(i + 1)) {
                    sb.insert(i + 1, 'X');
                }
            }
            if (sb.length() % 2 != 0) {
                sb.append('X');
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < sb.length(); i += 2) {
                char a = sb.charAt(i);
                char b = sb.charAt(i + 1);
                int[] posA = findPos(matrix, a);
                int[] posB = findPos(matrix, b);

                if (posA[0] == posB[0]) {
                    result.append(matrix[posA[0]][(posA[1] + 1) % 5]);
                    result.append(matrix[posB[0]][(posB[1] + 1) % 5]);
                } else if (posA[1] == posB[1]) {
                    result.append(matrix[(posA[0] + 1) % 5][posA[1]]);
                    result.append(matrix[(posB[0] + 1) % 5][posB[1]]);
                } else {
                    result.append(matrix[posA[0]][posB[1]]);
                    result.append(matrix[posB[0]][posA[1]]);
                }
            }
            return result.toString();
        } else {
            char[][] matrix = generatePlayfairMatrix(key);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i += 2) {
                char a = text.charAt(i);
                char b = text.charAt(i + 1);
                int[] posA = findPos(matrix, a);
                int[] posB = findPos(matrix, b);

                if (posA[0] == posB[0]) {
                    result.append(matrix[posA[0]][(posA[1] + 4) % 5]);
                    result.append(matrix[posB[0]][(posB[1] + 4) % 5]);
                } else if (posA[1] == posB[1]) {
                    result.append(matrix[(posA[0] + 4) % 5][posA[1]]);
                    result.append(matrix[(posB[0] + 4) % 5][posB[1]]);
                } else {
                    result.append(matrix[posA[0]][posB[1]]);
                    result.append(matrix[posB[0]][posA[1]]);
                }
            }
            return result.toString();
        }
    }

    public static String hill(String text, String key, int toogle) {
        if (toogle == 1) {
            text = text.toUpperCase().replaceAll("[^A-Z]", "");
            if (text.length() % 2 != 0) {
                text += "X";
            }

            int[][] keyMatrix = new int[2][2];
            int k = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    keyMatrix[i][j] = (key.charAt(k++) - 'A');
                }
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i += 2) {
                int r1 = text.charAt(i) - 'A';
                int r2 = text.charAt(i + 1) - 'A';
                int c1 = (keyMatrix[0][0] * r1 + keyMatrix[0][1] * r2) % 26;
                int c2 = (keyMatrix[1][0] * r1 + keyMatrix[1][1] * r2) % 26;
                result.append((char) (c1 + 'A'));
                result.append((char) (c2 + 'A'));
            }
            return result.toString();
        } else {
            text = text.toUpperCase().replaceAll("[^A-Z]", "");
            int[][] keyMatrix = new int[2][2];
            int k = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    keyMatrix[i][j] = (key.charAt(k++) - 'A');
                }
            }

            int det = (keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0]) % 26;
            if (det < 0) {
                det += 26;
            }

            int detInv = -1;
            for (int i = 0; i < 26; i++) {
                if ((det * i) % 26 == 1) {
                    detInv = i;
                    break;
                }
            }
            if (detInv == -1) {
                return "Error: Key not invertible";
            }

            int[][] invMatrix = new int[2][2];
            invMatrix[0][0] = (keyMatrix[1][1] * detInv) % 26;
            invMatrix[1][1] = (keyMatrix[0][0] * detInv) % 26;
            invMatrix[0][1] = (-keyMatrix[0][1] * detInv) % 26;
            invMatrix[1][0] = (-keyMatrix[1][0] * detInv) % 26;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (invMatrix[i][j] < 0) {
                        invMatrix[i][j] += 26;
                    }
                }
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i += 2) {
                int r1 = text.charAt(i) - 'A';
                int r2 = text.charAt(i + 1) - 'A';
                int c1 = (invMatrix[0][0] * r1 + invMatrix[0][1] * r2) % 26;
                int c2 = (invMatrix[1][0] * r1 + invMatrix[1][1] * r2) % 26;
                result.append((char) (c1 + 'A'));
                result.append((char) (c2 + 'A'));
            }
            return result.toString();
        }
    }

    public static String vigenere(String text, String key, int toogle) {
        if (toogle == 1) {
            StringBuilder result = new StringBuilder();
            key = key.toUpperCase();
            int j = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.isLetter(c)) {
                    boolean isUpper = Character.isUpperCase(c);
                    char p = (char) ((Character.toUpperCase(c) + key.charAt(j) - 2 * 'A') % 26 + 'A');
                    result.append(isUpper ? p : Character.toLowerCase(p));
                    j = (j + 1) % key.length();
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        } else {
            StringBuilder result = new StringBuilder();
            key = key.toUpperCase();
            int j = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.isLetter(c)) {
                    boolean isUpper = Character.isUpperCase(c);
                    char p = (char) ((Character.toUpperCase(c) - key.charAt(j) + 26) % 26 + 'A');
                    result.append(isUpper ? p : Character.toLowerCase(p));
                    j = (j + 1) % key.length();
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }
    }

    public static String railFence(String text, int key, int toogle) {
        if (toogle == 1) {
            int rails = key;
            char[][] fence = new char[rails][text.length()];
            for (int i = 0; i < rails; i++) {
                Arrays.fill(fence[i], '\n');
            }

            boolean down = false;
            int row = 0, col = 0;

            for (int i = 0; i < text.length(); i++) {
                if (row == 0 || row == rails - 1) {
                    down = !down;
                }
                fence[row][col++] = text.charAt(i);
                row += down ? 1 : -1;
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < rails; i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (fence[i][j] != '\n') {
                        result.append(fence[i][j]);
                    }
                }
            }
            return result.toString();
        } else {
            int rails = key;
            char[][] fence = new char[rails][text.length()];
            for (int i = 0; i < rails; i++) {
                Arrays.fill(fence[i], '\n');
            }

            boolean down = false;
            int row = 0, col = 0;

            for (int i = 0; i < text.length(); i++) {
                if (row == 0 || row == rails - 1) {
                    down = !down;
                }
                fence[row][col++] = '*';
                row += down ? 1 : -1;
            }

            int index = 0;
            for (int i = 0; i < rails; i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (fence[i][j] == '*' && index < text.length()) {
                        fence[i][j] = text.charAt(index++);
                    }
                }
            }

            StringBuilder result = new StringBuilder();
            row = 0;
            col = 0;
            down = false;
            for (int i = 0; i < text.length(); i++) {
                if (row == 0 || row == rails - 1) {
                    down = !down;
                }
                result.append(fence[row][col++]);
                row += down ? 1 : -1;
            }
            return result.toString();
        }
    }

    private static char[][] generatePlayfairMatrix(String key) {
        char[][] matrix = new char[5][5];
        String keyString = key.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I") + "ABCDEFGHIKLMNOPQRSTUVWXYZ";
        Set<Character> seen = new HashSet<>();
        int row = 0, col = 0;
        for (char c : keyString.toCharArray()) {
            if (!seen.contains(c)) {
                seen.add(c);
                matrix[row][col++] = c;
                if (col == 5) {
                    col = 0;
                    row++;
                }
            }
        }
        return matrix;
    }

    private static int[] findPos(char[][] matrix, char c) {
        if (c == 'J') {
            c = 'I';
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
