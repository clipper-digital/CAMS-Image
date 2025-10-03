package cams.memory;

public class MemoryInfo {

  public MemoryInfo() {}

  // Return Maximum Heap Size in Megabytes
  public static long getMaxHeapSize() {
    // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
    // Any attempt will result in an OutOfMemoryException.
    long heapMaxSize = Runtime.getRuntime().maxMemory();

    return heapMaxSize / 1048576;
  }

  public static long getCurrentHeapSize() {
    // Get current size of heap in bytes
    long heapSize = Runtime.getRuntime().totalMemory();

    return heapSize / 1048576;
  }


// Get amount of free memory within the heap in bytes. This size will increase
// after garbage collection and decrease as new objects are created.
// long heapFreeSize = Runtime.getRuntime().freeMemory();
}
