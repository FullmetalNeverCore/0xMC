package com.mcl;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class OSData {
    public static long getTotalMemory() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    
        long totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
    
        long totalMemory = totalPhysicalMemorySize / (1024 * 1024);

        return totalMemory; // returning total available memory;
    }

    public static Boolean isNull(String data) {
        return data == null;
    }
}
