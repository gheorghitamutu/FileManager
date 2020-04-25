package com.example.filemanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

// TODO: actions on path (add/remove one level)
public class Manager {
    private static String currentPath = getDefaultESDAbsolutePath();
    private static String currentFile = "";

    public static String getDefaultESDAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    public static String getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(String cf) {
        currentFile = cf;
    }

    public static boolean addPathLevel() {
        String tmpPath = (Paths.get(currentPath, currentFile)).toString();

        File f = new File(tmpPath);
        if (!f.isDirectory()) {
            return false;
        }

        currentPath = tmpPath;
        currentFile = "";

        return true;
    }

    public static boolean removePathLevel() {
        if (Objects.equals(currentPath, getDefaultESDAbsolutePath())) {
            return false;
        }

        File file = new File(currentPath);
        currentPath = file.getParent();
        currentFile = "";

        return true;
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

    public static void refreshFragment(MainActivity a, Fragment f) {
        final FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction();
        ft.detach(f);
        ft.attach(f);
        ft.commit();
    }
}
