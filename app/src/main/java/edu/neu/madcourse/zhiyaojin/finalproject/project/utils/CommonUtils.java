package edu.neu.madcourse.zhiyaojin.finalproject.project.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommonUtils {

    public static String getEmailPath(String email) {
        return email.replace('.', '|');
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}
