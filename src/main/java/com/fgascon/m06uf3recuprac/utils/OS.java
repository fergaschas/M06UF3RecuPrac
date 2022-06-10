package com.fgascon.m06uf3recuprac.utils;

import java.io.File;

public class OS {

    public static boolean isWindows(){
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static String getDiskDrive(){
        if(isWindows()) return "c:\\";

        return "/";
    }

    public static File homeDirectory(){
        return new File(System.getProperty("user.home"));
    }
}
