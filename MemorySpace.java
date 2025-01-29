/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {

    // A list of the memory blocks that are presently allocated
	private LinkedList allocatedList;

	// A list of memory blocks that are presently free
	private LinkedList freeList;

	/**
	 * Constructs a new managed memory space of a given maximal size.
	 * 
	 * @param maxSize
	 *            the size of the memory space to be managed
	 */
    public MemorySpace(int maxSize) {
        allocatedList = new LinkedList();
        freeList = new LinkedList();
        freeList.addLast(new MemoryBlock(0, maxSize));
    }

   /**
	 * Allocates a memory block of a requested length (in words). Returns the
	 * base address of the allocated block, or -1 if unable to allocate.
	 * 
	 * This implementation scans the freeList, looking for the first free memory block 
	 * whose length equals at least the given length. If such a block is found, the method 
	 * performs the following operations:
	 * 
	 * (1) A new memory block is constructed. The base address of the new block is set to
	 * the base address of the found free block. The length of the new block is set to the value 
	 * of the method's length parameter.
	 * 
	 * (2) The new memory block is appended to the end of the allocatedList.
	 * 
	 * (3) The base address and the length of the found free block are updated, to reflect the allocation.
	 * For example, suppose that the requested block length is 17, and suppose that the base
	 * address and length of the the found free block are 250 and 20, respectively.
	 * In such a case, the base address and length of of the allocated block
	 * are set to 250 and 17, respectively, and the base address and length
	 * of the found free block are set to 267 and 3, respectively.
	 * 
	 * (4) The new memory block is returned.
	 * 
	 * If the length of the found block is exactly the same as the requested length, 
	 * then the found block is removed from the freeList and appended to the allocatedList.
	 * 
	 * @param length
	 *        the length (in words) of the memory block that has to be allocated
	 * @return the base address of the allocated block, or -1 if unable to allocate
	 */
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

   /**
	 * Frees the memory block whose base address equals the given address.
	 * This implementation deletes the block whose base address equals the given 
	 * address from the allocatedList, and adds it at the end of the free list. 
	 * 
	 * @param baseAddress
	 *            the starting address of the block to freeList
	 */
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

    /**
	 * Performs defragmantation of this memory space.
	 * Normally, called by malloc, when it fails to find a memory block of the requested size.
	 * In this implementation Malloc does not call defrag.
	 */
    public boolean defrag() {
        if (freeList.getSize() < 2) {
            return true;
        }

        MemoryBlock[] arr = new MemoryBlock[freeList.getSize()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = freeList.getBlock(i);
        }

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j].baseAddress > arr[j + 1].baseAddress) {
                    // swap
                    MemoryBlock temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

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


    /**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
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
