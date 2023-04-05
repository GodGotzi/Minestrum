/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.utils;


import net.gotzi.minestrum.api.logging.LogLevel;

import java.util.logging.Logger;

public class MinestrumUtils {

    public static Logger LOGGER;

    public static String getCallerClassName() {
        return new Exception().getStackTrace()[2].getClassName();
    }

    public static String getCallerClassName(int index) {
        return new Exception().getStackTrace()[index].getClassName();
    }

    public static String getCallerMethodName() {
        return new Exception().getStackTrace()[2].getMethodName();
    }

    public static String getCallerMethodName(int index) {
        return new Exception().getStackTrace()[index].getMethodName();
    }

    public static void debugMessage(String s) {
        String cls = MinestrumUtils.getCallerClassName().equals(MinestrumUtils.class.getCanonicalName()) ?
                getCallerClassName(3) : getCallerClassName();
        try {
            LOGGER.log(LogLevel.DEBUG, s + "Class:" + Class.forName(cls));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void debugMessage(String s, int number) {
        debugMessage(s + "| debug-number: " + number);
    }

}