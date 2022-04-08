package com.hhoss.misc;

import java.lang.management.GarbageCollectorMXBean;  
import java.lang.management.ManagementFactory;  
import java.lang.management.MemoryMXBean;  
import java.util.List;  
import java.util.Map;  
public class JvmHook {  
    public static void main(String[] args) {  
        jvmExitHook();  
        System.out.println("aaaa");  
        try {  
            Thread.sleep(10000);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        System.exit(0);  
    }  
    public static void jvmExitHook() {  
        System.out.println("注册JVM Shutdown钩子方法———");  
        Runtime.getRuntime().addShutdownHook(new Thread() {  
            @Override  
            public void run() {  
                MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();  
                System.out.println("堆内存信息: " + memorymbean.getHeapMemoryUsage());  
                System.out.println("非堆内存信息: " + memorymbean.getNonHeapMemoryUsage());  
                Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();  
                List<GarbageCollectorMXBean> list = ManagementFactory.getGarbageCollectorMXBeans();  
                if (list != null && list.size() > 0) {  
                    for (GarbageCollectorMXBean gcBean : list) {  
                        System.out.println("垃圾收集器：" + gcBean.getName());  
                        System.out.println("gc count：" + gcBean.getCollectionCount());  
                        System.out.println("gc time：" + gcBean.getCollectionTime());  
                        gcBean.getCollectionCount();  
                    }  
                }  
                for (Thread t : map.keySet()) {  
                    System.out.println("线程名称：" + t.getName() + "，线程堆栈：");  
                    StackTraceElement[] ss = map.get(t);  
                    if (ss != null) {  
                        for (StackTraceElement s : ss) {  
                            System.out.println(s);  
                        }  
                    }  
                }  
            }  
        });  
    }  
}  