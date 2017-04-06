package com.clayoverwind.simpleioc.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author clayoverwind
 * @E-mail clayanddev@163.com
 * @version 2017/4/5
 */
public class ClassUtil {

    private static final Logger LOGGER = LogUtil.getLogger(ClassUtil.class);

    /**
     * 获取指定包名下的所有类
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassesByPackageName(ClassLoader classLoader, String packageName, boolean recursively) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        String packagePath = url.getPath().replaceAll(" ", "");
                        getClassesInPackageUsingFileProtocol(classes, classLoader, packagePath, packageName, recursively);
                    } else if ("jar".equals(protocol)) {
                        getClassesInPackageUsingJarProtocol(classes, classLoader, url, packageName, recursively);
                    } else {
                        LOGGER.warning(String.format("protocol[%s] not supported!", protocol));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classes;
    }

    private static void getClassesInPackageUsingJarProtocol(Set<Class<?>> classes, ClassLoader classLoader, URL url, String packageName, boolean recursively) throws IOException {
        String packagePath = packageName.replace(".", "/");
        System.out.println("---------getClassesInPackageUsingJarProtocol----------");
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        if (jarURLConnection != null) {
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                if (jarEntryName.startsWith(packagePath) && jarEntryName.endsWith(".class")) {
                    if (!recursively && jarEntryName.substring(packagePath.length() + 1).contains("/")) {
                        continue;
                    }
                    System.out.println(jarEntryName);
                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                    classes.add(loadClass(className, false, classLoader));
                }
            }
        }
        System.out.println("---------getClassesInPackageUsingJarProtocol----------");
    }

    private static void getClassesInPackageUsingFileProtocol(Set<Class<?>> classes, ClassLoader classLoader, String packagePath, String packageName, boolean recursively) {
        final File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class") || file.isDirectory()));
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (!StringUtil.isEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                classes.add(loadClass(className, false, classLoader));
            } else if (recursively) {
                String subPackagePath = fileName;
                if (!StringUtil.isEmpty(subPackagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (!StringUtil.isEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                getClassesInPackageUsingFileProtocol(classes, classLoader, subPackagePath, subPackageName, recursively);
            }
        }
    }

    /**
     *
     * @param className
     * @param isInitialized
     * @param classLoader
     * @return
     */
    public static Class<?> loadClass(String className, Boolean isInitialized, ClassLoader classLoader) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className, isInitialized, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return clazz;
    }
}
