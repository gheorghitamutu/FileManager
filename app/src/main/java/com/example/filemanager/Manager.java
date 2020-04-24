package com.example.filemanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

// TODO: actions on path (add/remove one level)
public class Manager {
    private static String currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String currentFile = "";

    public static String getCurrentPath() {
        return currentPath;
    }

    public static String getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(String cf) {
        currentFile = cf;
    }

    public static Activity getActivity()
            throws
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchFieldException {

        @SuppressLint("PrivateApi")
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThread = activityThreadClass.getMethod("currentActivityThread");
        Object activityThread = currentActivityThread.invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);

        Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
        if (activities == null)
            return null;

        for (Object activityRecord : activities.values()) {
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                return (Activity) activityField.get(activityRecord);
            }
        }

        return null;
    }
}
