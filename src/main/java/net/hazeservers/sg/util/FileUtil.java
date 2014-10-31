/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class FileUtil {

    public static boolean validate(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.exists();
    }

    public static boolean validate(File file, JavaPlugin plugin) {
        if (!file.exists() && plugin.getResource(file.getName()) != null) {
            plugin.saveResource(file.getName(), false);
        }
        return validate(file);
    }

    public static boolean copy(File source, File target) {
        try {
            copyFile(source, target);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        //Copy Directory Folder
        if (source.isDirectory()) {
            if (!target.exists()) target.mkdir();
            for (File file : source.listFiles())
                copyFile(file, new File(target, file.getName()));
            //Copy non-Directory File
        } else {
            int length;
            byte[] buffer = new byte[1024];
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(target);
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
        }
    }

    public static void deleteFolder(File folder) {
        if (!folder.exists()) return;
        for (File file : folder.listFiles())
            if (file.isDirectory()) deleteFolder(file);
            else file.delete();
        folder.delete();
    }
}
