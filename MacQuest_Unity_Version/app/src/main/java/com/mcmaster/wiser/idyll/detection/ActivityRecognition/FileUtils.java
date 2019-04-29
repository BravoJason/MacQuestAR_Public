package com.mcmaster.wiser.idyll.detection.ActivityRecognition;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 2017-03-28.
 */

public class FileUtils {

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static boolean fileExists(String path) {
        if (path == null) {
            return false;
        }

        File file = new File(path);
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean deleteFile(String path) {
        boolean ret = true;
        File f = new File(path);
        if (f.exists()) {
            ret = f.delete();
        }
        return ret;
    }

    public static File createFileIfNotExits(String filePath) throws IOException {
        if (!FileUtils.fileExists(filePath)) {
            return FileUtils.createFile(filePath);
        }
        return null;
    }

    public static File createFile(String destFileName) throws IOException {
        File f = new File(destFileName);
        if (!f.exists()) {
            if (f.getParentFile() != null && !f.getParentFile().exists()) {
                if (f.getParentFile().mkdirs()) {
                    f.createNewFile();
                }
            } else {
                f.createNewFile();
            }
        }
        return f;
    }


    public static boolean createDir(String destDirName) {
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

//    public static File createFile(String path) throws IOException {
//        File f = new File(path);
//        if (!f.exists()) {
//            if (f.getParentFile() != null && !f.getParentFile().exists()) {
//                if (f.getParentFile().mkdirs()) {
//                    f.createNewFile();
//                }
//            } else {
//                f.createNewFile();
//            }
//        }
//        return f;
//    }

    public boolean writeToFile(Context context, String mytext) {
        Log.i("FILE_WRITE", "SAVING");
        try {
            String MEDIA_MOUNTED = "mounted";
            String diskState = Environment.getExternalStorageState();
            if (diskState.equals(MEDIA_MOUNTED)) {
                File dir = new File(Environment.getExternalStorageDirectory(), "Qiang Particle Filter");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File outFile = new File(dir, "IPSWiSer2017.txt");

                //FileOutputStream fos = new FileOutputStream(outFile);

                BufferedWriter out = new BufferedWriter(new FileWriter(outFile, true));
                out.write(mytext + "\n");
                out.write("-----------------------------------\n");
                out.flush();
                out.close();

                return true;

            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            repl = m.replaceAll("");
        }
        return repl;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

}
