package ua.nanit.limbo.util;

import java.time.Duration;

public class DurationFormatter {

    public static String formatDuration(Duration duration) {
        if (duration == null || duration.isZero()) {
            return "0s";
        }

        long seconds = duration.getSeconds();

        long days = seconds / 86_400;
        seconds %= 86_400;

        long hours = seconds / 3_600;
        seconds %= 3_600;

        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0) {
            result.append(minutes).append("m ");
        }
        if (seconds > 0) {
            result.append(seconds).append("s ");
        }

        return result.toString().trim();
    }

}
