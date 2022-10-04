package ODIN.base.service.utils;

/**
 * MemoryUtil
 * 2022/4/23 zhoutao
 */
public class MemoryUtil {

    /**
     * memory consumption (Inaccurate)
     * unit:byte
     */
    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        return memory;
    }
}
