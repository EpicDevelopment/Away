package me.yirf.afk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class TimeUtil {
    public static String millisToString(long millis) {
        int seconds = Math.round((millis - System.currentTimeMillis()) / 1000);
        int minutes = seconds / 60;
        int hours = seconds / 3600;
        if (seconds < 60) {
            return seconds + "s";
        }
        if (minutes >= 1) {
            return minutes + "m";
        }
        if (hours >= 1) {
            return hours + "h";
        }

        return seconds + "s";
    }
}
