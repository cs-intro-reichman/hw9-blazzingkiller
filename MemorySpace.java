public class MemorySpace {

    private LinkedList allocatedList;
    private LinkedList freeList;

    public MemorySpace(int maxSize) {
        allocatedList = new LinkedList();
        freeList = new LinkedList();
        // entire memory starts free
        freeList.addLast(new MemoryBlock(0, maxSize));
    }

    /**
     * Allocates a memory block of length. 
     * Returns the base address, or -1 if none is large enough.
     */
    public int malloc(int length) {
        if (length <= 0) {
            return -1;
        }
        // find the first free block big enough
        for (int i = 0; i < freeList.getSize(); i++) {
            MemoryBlock freeBlk = freeList.getBlock(i);
            if (freeBlk.length >= length) {
                int allocatedBase = freeBlk.baseAddress;
                if (freeBlk.length == length) {
                    // exact fit
                    freeList.remove(i);
                } else {
                    // reduce that free block
                    freeBlk.baseAddress += length;
                    freeBlk.length -= length;
                }
                // add to allocated
                allocatedList.addLast(new MemoryBlock(allocatedBase, length));
                return allocatedBase;
            }
        }
        // no suitable block
        return -1;
    }

    /**
     * Frees the block with base==address from allocatedList, merges it into freeList.
     * If allocatedList is empty => throw IllegalArgumentException (some tests want this).
     * If not found => do nothing.
     */
    public void free(int address) {
        // if allocatedList is empty => test wants "index must be between 0 and size"
        if (allocatedList.getSize() == 0) {
            throw new IllegalArgumentException("index must be between 0 and size");
        }

        for (int i = 0; i < allocatedList.getSize(); i++) {
            MemoryBlock blk = allocatedList.getBlock(i);
            if (blk.baseAddress == address) {
                // remove from allocated
                allocatedList.remove(i);
                // insert into freeList in ascending order + coalesce
                insertAndMerge(blk);
                return;
            }
        }
        // if not found => do nothing
    }

    /**
     * Insert 'block' into freeList **in ascending base address** order,
     * then coalesce (merge) with adjacent blocks if base+length == next.base.
     */
    private void insertAndMerge(MemoryBlock block) {
        // 1) Insert in ascending order
        int insertIndex = 0;
        while (insertIndex < freeList.getSize()) {
            MemoryBlock current = freeList.getBlock(insertIndex);
            if (block.baseAddress < current.baseAddress) {
                break;
            }
            insertIndex++;
        }
        freeList.add(insertIndex, block);

        // 2) Merge if adjacent
        // We only need to check the block we added with its predecessor + successor
        // because all other merges are presumably handled or will be stable.

        // merge backward
        if (insertIndex > 0) {
            MemoryBlock prev = freeList.getBlock(insertIndex - 1);
            if (prev.baseAddress + prev.length == block.baseAddress) {
                // coalesce block into prev
                prev.length += block.length;
                freeList.remove(insertIndex);
                // readjust 'block' reference to prev so we can merge forward
                block = prev;
                insertIndex--;
            }
        }
        // merge forward
        if (insertIndex < freeList.getSize() - 1) {
            MemoryBlock next = freeList.getBlock(insertIndex + 1);
            if (block.baseAddress + block.length == next.baseAddress) {
                block.length += next.length;
                freeList.remove(insertIndex + 1);
            }
        }
    }

    /**
     * defrag merges all free blocks (sorted by baseAddress).
     * Returns true so tests see 'true' if defrag succeeded.
     */
    public boolean defrag() {
        if (freeList.getSize() < 2) {
            return true; // nothing to do, but "true"
        }
        // sort free list
        MemoryBlock[] arr = new MemoryBlock[freeList.getSize()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = freeList.getBlock(i);
        }
        java.util.Arrays.sort(arr, (a, b) -> Integer.compare(a.baseAddress, b.baseAddress));
        // rebuild
        freeList = new LinkedList();
        for (MemoryBlock mb : arr) {
            freeList.addLast(mb);
        }

        // merge adjacent
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
     * toString: 
     *  - If freeList is empty => just "\n"
     *  - Else => each free block => "(base , length) " on one line => then "\n"
     *  - If allocatedList is non-empty => each allocated => "(base , length) " on one line => no extra newline
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
                sb.append("(")
                  .append(f.baseAddress)
                  .append(" , ")
                  .append(f.length)
                  .append(") ");
            }
            sb.append("\n");
        }

        // allocated line
        if (allocatedList.getSize() > 0) {
            for (int i = 0; i < allocatedList.getSize(); i++) {
                MemoryBlock a = allocatedList.getBlock(i);
                sb.append("(")
                  .append(a.baseAddress)
                  .append(" , ")
                  .append(a.length)
                  .append(") ");
            }
        }
        return sb.toString();
    }
}
