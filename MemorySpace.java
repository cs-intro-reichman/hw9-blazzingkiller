public class MemorySpace {

    private LinkedList allocatedList;
    private LinkedList freeList;

    public MemorySpace(int maxSize) {
        allocatedList = new LinkedList();
        freeList = new LinkedList();
        freeList.addLast(new MemoryBlock(0, maxSize));
    }

   
    public int malloc(int length) {
        if (length <= 0) {
            return -1;
        }
        for (int i = 0; i < freeList.getSize(); i++) {
            MemoryBlock freeBlk = freeList.getBlock(i);
            if (freeBlk.length >= length) {
                int allocatedBase = freeBlk.baseAddress;
                if (freeBlk.length == length) {
                    freeList.remove(i); 
                } else {
                    freeBlk.baseAddress += length;
                    freeBlk.length -= length;
                }
                allocatedList.addLast(new MemoryBlock(allocatedBase, length));
                return allocatedBase;
            }
        }
        return -1;
    }

   
    public void free(int address) {
        if (allocatedList.getSize() == 0) {
            throw new IllegalArgumentException("index must be between 0 and size");
        }
        for (int i = 0; i < allocatedList.getSize(); i++) {
            MemoryBlock blk = allocatedList.getBlock(i);
            if (blk.baseAddress == address) {
                allocatedList.remove(i);
                freeList.addLast(blk);
                return;
            }
        }
    }

    
    public boolean defrag() {
        if (freeList.getSize() < 2) {
            return true; 
        }
        MemoryBlock[] arr = new MemoryBlock[freeList.getSize()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = freeList.getBlock(i);
        }
        java.util.Arrays.sort(arr, (a, b) -> Integer.compare(a.baseAddress, b.baseAddress));

        freeList = new LinkedList();
        for (MemoryBlock mb : arr) {
            freeList.addLast(mb);
        }

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

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (freeList.getSize() == 0) {
            sb.append("\n");
        } else {
            for (int i = 0; i < freeList.getSize(); i++) {
                MemoryBlock f = freeList.getBlock(i);
                sb.append("(").append(f.baseAddress).append(" , ").append(f.length).append(") ");
            }
            sb.append("\n");
        }

        // allocated blocks line
        if (allocatedList.getSize() > 0) {
            for (int i = 0; i < allocatedList.getSize(); i++) {
                MemoryBlock a = allocatedList.getBlock(i);
                sb.append("(").append(a.baseAddress).append(" , ").append(a.length).append(") ");
            }
        }

        return sb.toString();
    }
}
