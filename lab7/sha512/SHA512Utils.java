public class SHA512Utils {

    public static String toBinary(String message) {
        StringBuilder binary = new StringBuilder();
        for (char c : message.toCharArray()) {
            binary.append(String.format("%8s",
                    Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    public static String preprocess(String message) {

        String binaryMsg = toBinary(message);
        long originalLength = binaryMsg.length();

        StringBuilder padded = new StringBuilder(binaryMsg);
        padded.append('1');

        while (padded.length() % 1024 != 896) {
            padded.append('0');
        }

        String lengthBits = String.format("%128s",
                Long.toBinaryString(originalLength)).replace(' ', '0');

        padded.append(lengthBits);

        return padded.toString();
    }

    public static String[] extractMessageSchedule(String block) {

        String[] words = new String[16];

        for (int i = 0; i < 16; i++) {
            words[i] = block.substring(i * 64, (i + 1) * 64);
        }

        return words;
    }
}