package com.bigdata.sampler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuanhailong on 2019/8/29.
 */
public class Hardware {
    private static final Logger LOG = LoggerFactory.getLogger(Hardware.class);

    private static final String LINUX_MEMORY_INFO_PATH = "/proc/meminfo";

    private static final Pattern LINUX_MEMORY_REGEX = Pattern.compile("^MemTotal:\\s*(\\d+)\\s+kB$");


    //获取系统CPU数量
    public static int getNumberCPUCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取系统内存
     * @return
     */
    public static long getSizeOfPhysicalMemory() {
        //在Oracle JVM下系统有可能直接告诉我们系统内存，因此首先通过这种方式尝试获取内存
        try {
            Class<?> clazz = Class.forName("com.sun.management.OperatingSystemMXBean");
            Method method = clazz.getMethod("getTotalPhysicalMemorySize");
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

            //检查是否是sun提供的MXBean
            if (clazz.isInstance(operatingSystemMXBean)) {
                return (Long) method.invoke(operatingSystemMXBean);
            }
        }
        catch (ClassNotFoundException e) {
            // this happens on non-Oracle JVMs, do nothing and use the alternative code paths
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.warn("Access to physical memory size: " +
                    "com.sun.management.OperatingSystemMXBean incompatibly changed.", e);
        }

        if(System.getProperty("os.name").toLowerCase().startsWith("linux")){  //linux system
            return getSizeOfPhysicalMemoryForLinux();
        }else if (System.getProperty("os.name").toLowerCase().startsWith("window")){
            return getSizeOfPhysicalMemoryForWindows();
        }else if (System.getProperty("os.name").toLowerCase().startsWith("mac")){
            return getSizeOfPhysicalMemoryForMac();
        }else if (System.getProperty("os.name").toLowerCase().startsWith("freebsd")){
            return getSizeOfPhysicalMemoryForFreeBSD();
        }else{
            return -1;  //如果都不是返回-1
        }



    }

    /**
     * Linux系统获取内存
     * @return
     */
    private static long getSizeOfPhysicalMemoryForLinux() {
        try (BufferedReader lineReader = new BufferedReader(new FileReader(LINUX_MEMORY_INFO_PATH))) {
            String line;
            while ((line = lineReader.readLine()) != null) {
                Matcher matcher = LINUX_MEMORY_REGEX.matcher(line);
                if (matcher.matches()) {
                    String totalMemory = matcher.group(1);
                    return Long.parseLong(totalMemory) * 1024L; // 换算为KB
                }
            }
            return -1;
        }
        catch (Exception e) {
            return -1;
        }
    }

    /**
     * Mac系统获取内存
     */
    private static long getSizeOfPhysicalMemoryForMac() {
        BufferedReader bi = null;
        try {
            Process proc = Runtime.getRuntime().exec("sysctl hw.memsize");
            bi = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = bi.readLine()) != null) {
                if (line.startsWith("hw.memsize")) {
                    long memsize = Long.parseLong(line.split(":")[1].trim());
                    bi.close();
                    proc.destroy();
                    return memsize;
                }
            }
        } catch (Throwable t) {
            return -1;
        } finally {
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException ignored) {}
            }
        }
        return -1;
    }

    /**
     * FreeBSD系统
     */
    private static long getSizeOfPhysicalMemoryForFreeBSD() {
        BufferedReader bi = null;
        try {
            Process proc = Runtime.getRuntime().exec("sysctl hw.physmem");

            bi = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line;
            while ((line = bi.readLine()) != null) {
                if (line.startsWith("hw.physmem")) {
                    long memsize = Long.parseLong(line.split(":")[1].trim());
                    bi.close();
                    proc.destroy();
                    return memsize;
                }
            }
            return -1;
        }
        catch (Throwable t) {
            return -1;
        }
        finally {
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Window 系统
     */
    private static long getSizeOfPhysicalMemoryForWindows() {
        BufferedReader bi = null;
        try {
            Process proc = Runtime.getRuntime().exec("wmic memorychip get capacity");
            bi = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = bi.readLine();
            if (line == null) {
                return -1L;
            }
            if (!line.startsWith("Capacity")) {
                return -1L;
            }
            long sizeOfPhyiscalMemory = 0L;
            while ((line = bi.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                line = line.replaceAll(" ", "");
                sizeOfPhyiscalMemory += Long.parseLong(line);
            }
            return sizeOfPhyiscalMemory;
        }
        catch (Throwable t) {
            return -1L;
        }
        finally {
            if (bi != null) {
                try {
                    bi.close();
                } catch (Throwable ignored) {}
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(Hardware.getSizeOfPhysicalMemory());
    }
}
