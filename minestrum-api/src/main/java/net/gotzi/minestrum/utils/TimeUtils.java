package net.gotzi.minestrum.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String getSimpleDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getSimpleDateForFile() {
        return new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date());
    }



}
