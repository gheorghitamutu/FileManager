package com.example.filemanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

// TODO: use ContentResolver for SDCard actions
public class Manager {
    private static String currentPath = getDefaultESDAbsolutePath();
    private static String currentFile = "";
    private static String currentObjPathForAction = "";
    private static String currentObjFilenameForAction = "";
    private static String currentAction = "";

    public static void setActionAndSource(String action) {
        currentObjPathForAction = (Paths.get(currentPath, currentFile)).toString();
        currentObjFilenameForAction = currentFile;
        currentAction = action;
    }

    private static String getDefaultESDAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private static String getDefaultSDCardAbsolutePath() {
        String removableStoragePath = "";
        File[] fileList = new File("/storage/").listFiles();
        for (File file : Objects.requireNonNull(fileList)) {
            if (!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead()) {
                File[] contents = file.listFiles();
                if (contents == null) {
                    continue;
                } else if (contents.length == 0) {
                    continue;
                }

                return file.getAbsolutePath();
            }
        }

        return removableStoragePath;
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    public static void setCurrentFile(String cf) {
        currentFile = cf;
    }

    public static boolean addPathLevel() {
        String fullPath = (Paths.get(currentPath, currentFile)).toString();

        File f = new File(fullPath);
        if (!f.isDirectory()) {
            return false;
        }

        currentPath = fullPath;
        currentFile = "";

        return true;
    }

    static boolean removePathLevel() {
        if (Objects.equals(currentPath, getDefaultESDAbsolutePath()) ||
                Objects.equals(currentPath, getDefaultSDCardAbsolutePath())) {
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

    public static boolean deleteFSObject() {
        boolean result = false;

        String fullPath = (Paths.get(currentPath, currentFile)).toString();
        File f = new File(fullPath);
        if (f.exists()) {
            boolean sdCard = false;

            String defaultESDPath = getDefaultESDAbsolutePath();
            if (!currentPath.contains(defaultESDPath)) {
                sdCard = true;
            }

            result = deleteRecursive(f, sdCard);
        }

        return result;
    }

    private static boolean deleteFSObject(String path) {
        boolean result = false;

        File f = new File(path);
        if (f.exists()) {
            boolean sdCard = false;

            String defaultESDPath = getDefaultESDAbsolutePath();
            if (!currentPath.contains(defaultESDPath)) {
                sdCard = true;
            }

            result = deleteRecursive(f, sdCard);
        }

        return result;
    }

    private static boolean deleteRecursive(File objPath, boolean sdCard) {
        boolean result = true;

        if (objPath.isDirectory()) {
            for (File child : Objects.requireNonNull(objPath.listFiles())) {
                result = result && deleteRecursive(child, sdCard);
            }
        }

        // this handles external sd card deletion (files only)
        if (sdCard) {
            final String where = MediaStore.MediaColumns.DATA + "=?";
            final String[] selectionArgs = new String[]{
                    objPath.getAbsolutePath()
            };

            MainActivity ma = null;
            try {
                ma = (MainActivity) Manager.getActivity();
            } catch (ClassNotFoundException |
                    NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException |
                    NoSuchFieldException e) {
                e.printStackTrace();
            }

            final ContentResolver contentResolver = Objects.requireNonNull(ma).getContentResolver();
            final Uri filesUri = MediaStore.Files.getContentUri("external");

            contentResolver.delete(filesUri, where, selectionArgs);

            if (objPath.exists()) {
                contentResolver.delete(filesUri, where, selectionArgs);
            }

            result = result && !objPath.exists();
        } else {
            result = result && objPath.delete();
        }

        return result;
    }

    static boolean createNewFile(String name) {
        boolean result = false;
        String fullPath = (Paths.get(currentPath, name)).toString();

        File file = new File(fullPath);
        if (!file.exists()) {
            try {
                result = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    static boolean createNewFolder(String name) {
        boolean result = false;
        String fullPath = (Paths.get(currentPath, name)).toString();

        File file = new File(fullPath);
        if (!file.exists()) {
            result = file.mkdirs();
        }

        return result;
    }

    // this is so bad..
    public static void renameFSObject() {
        MainActivity ma = null;
        try {
            ma = (MainActivity) Manager.getActivity();
        } catch (ClassNotFoundException |
                NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException |
                NoSuchFieldException e) {
            e.printStackTrace();
        }

        final MainActivity finalMa = ma;

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(ma));
        builder.setTitle("Rename FS Object");

        final EditText input = new EditText(ma);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean result = false;

                String newName = input.getText().toString();

                String oldPath = (Paths.get(currentPath, currentFile)).toString();
                File oldFolder = new File(oldPath);

                String newPath = (Paths.get(currentPath, newName)).toString();
                File newFolder = new File(newPath);

                if (oldFolder.exists() && !newFolder.exists()) {
                    result = oldFolder.renameTo(newFolder);
                }

                if (result) {
                    Toast.makeText(finalMa, "Action succeeded!", Toast.LENGTH_SHORT).show();
                    Manager.refreshFragment(Objects.requireNonNull(finalMa), MainActivity.getCurrentNavigationFragment());
                } else {
                    Toast.makeText(finalMa, "Action failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    static void resetCurrentPathToESD() {
        currentPath = getDefaultESDAbsolutePath();
    }

    static void resetCurrentPathSDCard() {
        currentPath = getDefaultSDCardAbsolutePath();
    }

    static boolean doFSAction() {
        boolean result = false;

        File src = new File(currentObjPathForAction);

        String dstPath = Paths.get(currentPath, currentObjFilenameForAction).toString();
        File dst = new File(dstPath);

        if (src.exists() && !dst.exists()) {
            try {
                if (currentAction.equals("Copy")) {
                    copyFileOrDirectory(src, dst); // if doesn't throw then succeeded
                    result = true;
                } else if (currentAction.equals("Move")) {
                    result = src.renameTo(dst);
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }

        return result;
    }

    private static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static void copyFileOrDirectory(File src, File dst) throws IOException {

        if (src.isDirectory()) {
            boolean result = true;
            if (!dst.exists()) {
                result = dst.mkdirs();
            }

            if (!result) {
                return;
            }

            String[] children = src.list();
            for (String child : Objects.requireNonNull(children)) {
                copyFileOrDirectory(new File(src, child), new File(
                        dst, child));
            }
        } else {
            copyFile(src, dst);
        }
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) throws Exception {
        File src = new File(filePath);
        FileInputStream fin = new FileInputStream(src);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public static String getStringFromCurrentFile() {
        String fullPath = (Paths.get(currentPath, currentFile)).toString();
        try {
            return getStringFromFile(fullPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCurrentFilename() {
        return currentFile;
    }

    public static boolean saveInFile(String content) {
        String fullPath = (Paths.get(currentPath, currentFile)).toString();
        String fullPathTmp = fullPath + ".tmp";
        if (createNewFile(fullPathTmp)) {
            return false;
        }

        File fTmp = new File(fullPathTmp);
        try (FileOutputStream stream = new FileOutputStream(fTmp)) {
            stream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            deleteFSObject(fullPathTmp);
            return false;
        }

        if (!deleteFSObject()) {
            return false;
        }

        File f = new File(fullPath);
        return fTmp.renameTo(f);
    }
}
