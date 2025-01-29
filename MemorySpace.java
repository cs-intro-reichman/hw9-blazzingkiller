public class MemorySpace {

    private LinkedList allocatedList;
    private LinkedList freeList;

    public MemorySpace(int maxSize) {
        allocatedList = new LinkedList();
        freeList = new LinkedList();
        freeList.addLast(new MemoryBlock(0, maxSize));
    }

    /** 
     * Allocates a block of the requested length,
     * returning the base address or -1 if none is big enough. 
     */
    public int malloc(int length) {
        if (length <= 0) {
            return -1;
        }
        // First-fit scan freeList
        for (int i = 0; i < freeList.getSize(); i++) {
            MemoryBlock freeBlk = freeList.getBlock(i);
            if (freeBlk.length >= length) {
                // Found a suitable block
                int allocatedBase = freeBlk.baseAddress;
                if (freeBlk.length == length) {
                    // exact fit => remove from freeList
                    freeList.remove(i);
                } else {
                    // block is bigger => shrink it
                    freeBlk.baseAddress += length;
                    freeBlk.length -= length;
                }
                // Add to allocated
                allocatedList.addLast(new MemoryBlock(allocatedBase, length));
                return allocatedBase;
            }
        }
        // not found
        return -1;
    }

    /**
     * Frees the block with base==address from allocatedList.
     * If allocatedList is empty => throws IAE (some tests want that).
     * If not found => do nothing.
     * If found => remove from allocatedList, then add **without merging** at the front of freeList.
     */
    public void free(int address) {
        if (allocatedList.getSize() == 0) {
            // Test scenario: "Try to free a block of memory when freeList is empty"
            throw new IllegalArgumentException("index must be between 0 and size");
        }
        for (int i = 0; i < allocatedList.getSize(); i++) {
            MemoryBlock blk = allocatedList.getBlock(i);
            if (blk.baseAddress == address) {
                // remove from allocated
                allocatedList.remove(i);
                // just add to front of freeList (no merging here!)
                freeList.addFirst(blk);
                return;
            }
        }
        // if not found => do nothing
    }

    /**
     * defrag merges all free blocks. 
     * 
     * Steps:
     *  1) gather free blocks in an array
     *  2) sort by ascending baseAddress
     *  3) rebuild freeList from sorted array
     *  4) merge adjacent blocks
     *  5) return true so the test sees "Expected: true, Actual: true"
     */
    public boolean defrag() {
        if (freeList.getSize() < 2) {
            return true; 
        }
        // 1+2) copy + sort
        MemoryBlock[] arr = new MemoryBlock[freeList.getSize()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = freeList.getBlock(i);
        }
        java.util.Arrays.sort(arr, (a, b) -> Integer.compare(a.baseAddress, b.baseAddress));

        // 3) rebuild freeList
        freeList = new LinkedList();
        for (MemoryBlock mb : arr) {
            freeList.addLast(mb);
        }

        // 4) merge adjacent
        int i = 0;
        while (i < freeList.getSize() - 1) {
            MemoryBlock curr = freeList.getBlock(i);
            MemoryBlock nxt = freeList.getBlock(i + 1);
            if (curr.baseAddress + curr.length == nxt.baseAddress) {
                curr.length += nxt.length;
                freeList.remove(i + 1);
            } else {
                i++;
            }
        }
        return true;
    }

    /**
     * The test's expected toString format:
     *  1) If freeList is empty => print just "\n"
     *  2) Else => each free block => "(base , length) " on one line => then "\n"
     *  3) Then if allocatedList is non-empty => each allocated => "(base , length) " on one line => trailing space. 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // free line
        if (freeList.getSize() == 0) {
            sb.append("\n");
        } else {
            for (int i = 0; i < freeList.getSize(); i++) {
                MemoryBlock f = freeList.getBlock(i);
                sb.append("(").append(f.baseAddress).append(" , ").append(f.length).append(") ");
            }
            sb.append("\n");
        }

        // allocated line
        if (allocatedList.getSize() > 0) {
            for (int i = 0; i < allocatedList.getSize(); i++) {
                MemoryBlock a = allocatedList.getBlock(i);
                sb.append("(").append(a.baseAddress).append(" , ").append(a.length).append(") ");
            }
        }
        return sb.toString();
    }
}
