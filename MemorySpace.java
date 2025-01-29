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
		// initiallizes an empty list of allocated blocks.
		allocatedList = new LinkedList();
	    // Initializes a free list containing a single block which represents
	    // the entire memory. The base address of this single initial block is
	    // zero, and its length is the given memory size.
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
			// Invalid size
			if (length <= 0) {
				return -1;
			}
	
			for (int i = 0; i < freeList.getSize(); i++) {
				MemoryBlock freeBlock = freeList.getBlock(i);
				if (freeBlock.length >= length) {
					int allocatedBase = freeBlock.baseAddress;
	
					if (freeBlock.length == length) {
						freeList.remove(i);
						allocatedList.addLast(new MemoryBlock(allocatedBase, length));
					} else {
						allocatedList.addLast(new MemoryBlock(allocatedBase, length));
						freeBlock.baseAddress += length;
						freeBlock.length -= length;
					}
					return allocatedBase; 
				}
			}
	
			return -1;
		}
	
		/**
		 * Frees the block whose base address == 'address'.
		 * Removes that block from 'allocatedList' and puts it at the front of 'freeList'.
		 */
		public void free(int address) {
			for (int i = 0; i < allocatedList.getSize(); i++) {
				MemoryBlock block = allocatedList.getBlock(i);
				if (block.baseAddress == address) {
					allocatedList.remove(i);
					freeList.addFirst(block);
					return;
				}
			}
		}

		public void defrag() {
			if (freeList.getSize() < 2) {
				return;
			}
		
			for (int i = 0; i < freeList.getSize() - 1; i++) {
				int minIndex = i;
				MemoryBlock minBlock = freeList.getBlock(i);
				
				for (int j = i + 1; j < freeList.getSize(); j++) {
					MemoryBlock candidate = freeList.getBlock(j);
					if (candidate.baseAddress < minBlock.baseAddress) {
						minIndex = j;
						minBlock = candidate;
					}
				}
				if (minIndex != i) {
					MemoryBlock currentBlock = freeList.getBlock(i);
		
					freeList.remove(minIndex);
					freeList.add(minIndex, currentBlock);
		
					freeList.remove(i);
					freeList.add(i, minBlock);
				}
			}
		
			int i = 0;
			while (i < freeList.getSize() - 1) {
				MemoryBlock current = freeList.getBlock(i);
				MemoryBlock next = freeList.getBlock(i + 1);
		
				if (current.baseAddress + current.length == next.baseAddress) {
					current.length += next.length;
					freeList.remove(i + 1);
				} else {
					i++;
				}
			}
		}
		
	
		/**
		 * A textual representation of this memory space, matching the test's exact format.
		 *  - First line: free blocks (no spaces between them)
		 *  - Second line (only if allocatedList is non-empty):
		 *        allocated blocks separated by a single space
		 *  - Each line ends with a newline.
		 */
		@Override
public String toString() {
    // For freeList, we want exactly: "(base , length) (base2 , length2) ... \n"
    // with a trailing space, then a newline.
    // For allocatedList, we want exactly: "(base , length) (base2 , length2) ... " with trailing space. Then no extra newline?

    StringBuilder sb = new StringBuilder();

    if (freeList.getSize() > 0) {
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

	