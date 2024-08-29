//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pl.olafcio.lprm;

import org.jetbrains.annotations.NotNull;

public final class GradientUtil {
    public GradientUtil() {
    }

    public static @NotNull String generateMessage(@NotNull String head, @NotNull String tail, @NotNull String message) {
        if (message.isEmpty()) {
            return message;
        } else {
            int[] hex = resolveDefinedHexInts(head, tail);
            double perChar = 100.0 / (double)message.length();
            var builder = new StringBuilder();
            double current = 0.0;
            var charlist = message.toCharArray();

            for (char c : charlist) {
                int[] values = generatePercentageRGB(hex, current / 100.0);
                builder.append("&#").append(String.format("%02X%02X%02X", values[0], values[1], values[2])).append(c);
                current = Math.min(100.0, current + perChar);
            }

            return builder.toString();
        }
    }

    public static int @NotNull [] resolveDefinedHexInts(@NotNull String head, @NotNull String tail) {
        int hexOneR = Integer.parseInt(head.substring(1, 3), 16);
        int hexOneG = Integer.parseInt(head.substring(3, 5), 16);
        int hexOneB = Integer.parseInt(head.substring(5, 7), 16);
        int hexTwoR = Integer.parseInt(tail.substring(1, 3), 16);
        int hexTwoG = Integer.parseInt(tail.substring(3, 5), 16);
        int hexTwoB = Integer.parseInt(tail.substring(5, 7), 16);
        return new int[]{hexOneR, hexOneG, hexOneB, hexTwoR, hexTwoG, hexTwoB};
    }

    public static int @NotNull [] generatePercentageRGB(int @NotNull [] hex, double percentage) {
        int valueR = (int)((double)hex[0] + (double)(hex[3] - hex[0]) * percentage);
        int valueG = (int)((double)hex[1] + (double)(hex[4] - hex[1]) * percentage);
        int valueB = (int)((double)hex[2] + (double)(hex[5] - hex[2]) * percentage);
        return new int[]{valueR, valueG, valueB};
    }
}
