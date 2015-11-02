package com.zcloud.monitor.alarm.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: yuyangning
 * Date: 1/30/15
 * Time: 11:18 AM
 */
public class CommonUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    private static Pattern pattern = Pattern.compile("^\\[(.*)\\]YYYY\\.MM\\.DD$");


    public static String contact(String... args) {
        StringBuilder str = new StringBuilder();
        if (args == null) return null;
        for (String arg : args) {

            str.append(arg);
        }
        return str.toString();
    }

    public static String indexString(String regex) {
        Matcher matcher = pattern.matcher(regex);
        if (matcher.find()) {
            return matcher.group(1) + format.format(new Date());
        }
        return regex;
    }

    public static String getConfigPath() {
        String path = (String) System.getProperties().get("app.home");
        if (StringUtils.isNotBlank(path)) {
            return path + "/etc";
        }
        return null;
    }

    public static InputStream getResourceStream(String fileName) {
        try {
            String config = getConfigPath();
            if (StringUtils.isNotEmpty(config)) {
                File file = new File(getConfigPath(), fileName);
                if (file.exists()) return new FileInputStream(file);
            }
        } catch (FileNotFoundException e) {
            return CommonUtils.class.getClassLoader().getResourceAsStream(fileName);
        }
        return CommonUtils.class.getClassLoader().getResourceAsStream(fileName);
    }

    public static void main(String[] args) {
        String path = (String) System.getProperties().get("env.home");
        System.out.println("path:"+path);
    }
}
