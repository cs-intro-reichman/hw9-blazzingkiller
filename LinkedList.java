/**
 * Represents a list of Nodes. 
 */
public class LinkedList {
	
	private Node first; // pointer to the first element of this list
	private Node last;  // pointer to the last element of this list
	private int size;   // number of elements in this list
	
	/**
	 * Constructs a new list.
	 */ 
	public LinkedList () {
		first = null;
		last = first;
		size = 0;
	}
	
	/**
	 * Gets the node located at the given index in this list. 
	 * 
	 * @param index
	 *        the index of the node to retrieve, between 0 and size
	 * @throws IllegalArgumentException
	 *         if index is negative or greater than the list's size
	 * @return the node at the given index
	 */		
	public Node getNode(int index) {
		if (index < 0 || index > size) {
			throw new IllegalArgumentException(
					"index must be between 0 and size");
		}
		Node current = first; // Start at the first node
    for (int i = 0; i < index; i++) {
        current = current.next; // Move to the next node
    }
    
    return current; 

	}
	
	/**
	 * Creates a new Node object that points to the given memory block, 
	 * and inserts the node at the given index in this list.
	 * <p>
	 * If the given index is 0, the new node becomes the first node in this list.
	 * <p>
	 * If the given index equals the list's size, the new node becomes the last 
	 * node in this list.
     * <p>
	 * The method implementation is optimized, as follows: if the given 
	 * index is either 0 or the list's size, the addition time is O(1). 
	 * 
	 * @param block
	 *        the memory block to be inserted into the list
	 * @param index
	 *        the index before which the memory block should be inserted
	 * @throws IllegalArgumentException
	 *         if index is negative or greater than the list's size
	 */
	public void add(int index, MemoryBlock block) {
		if (index < 0 || index > size) {
			throw new IllegalArgumentException(
					"index must be between 0 and size");
		}
		Node newNode = new Node(block);

        // Case 1: Insert at head (index == 0)
        if (index == 0) {
            newNode.next = first;
            first = newNode;
            // If the list was empty, 'first' and 'last' should be the same
            if (size == 0) {
                last = newNode;
            }
        }
        // Case 2: Insert at the end (index == size)
        else if (index == size) {
            last.next = newNode;
            last = newNode;
        }
        // Case 3: Insert in the middle
        else {
            Node prev = getNode(index - 1);
            newNode.next = prev.next;
            prev.next = newNode;
        }

        size++;
    }

	/**
	 * Creates a new node that points to the given memory block, and adds it
	 * to the end of this list (the node will become the list's last element).
	 * 
	 * @param block
	 *        the given memory block
	 */
	public void addLast(MemoryBlock block) {
		Node newNode = new Node(block);

        // If the list is empty, just set first/last to this new node
        if (size == 0) {
            first = newNode;
            last = newNode;
        } else {
            // Link after the current 'last'
            last.next = newNode;
            last = newNode;
        }
        size++;
    }

	
	
	/**
	 * Creates a new node that points to the given memory block, and adds it 
	 * to the beginning of this list (the node will become the list's first element).
	 * 
	 * @param block
	 *        the given memory block
	 */
	public void addFirst(MemoryBlock block) {
		Node newNode = new Node(block);

		if (first == null) {
			first = newNode;
			last = newNode;
	} else {
		newNode.next = first;
		first = newNode;
		}
		size++;
	}

	/**
	 * Gets the memory block located at the given index in this list.
	 * 
	 * @param index
	 *        the index of the retrieved memory block
	 * @return the memory block at the given index
	 * @throws IllegalArgumentException
	 *         if index is negative or greater than or equal to size
	 */
	public MemoryBlock getBlock(int index) {
		if (index < 0 || index > size) {
			throw new IllegalArgumentException(
					"index must be between 0 and size");
		}		
		Node current = first;
		for (int i = 0; i < index; i++) {
    		current = current.next;
			}
			// Now current points to the node at the specified index
		return current.block;
	}

	/**
	 * Gets the index of the node pointing to the given memory block.
	 * 
	 * @param block
	 *        the given memory block
	 * @return the index of the block, or -1 if the block is not in this list
	 */
	public int indexOf(MemoryBlock block) {
		if (block == null){	
				return -1;
		}
		Node current = first;
		for(int i = 0; i < size; i++){
			if (current.block == block){
				return i;
			}
			current = current.next;
		}
		return -1;
	}

	/**
	 * Removes the given node from this list.	
	 * 
	 * @param node
	 *        the node that will be removed from this list
	 */
	public void remove(Node node) {
		if (node == null || first == null) {
			return;
		}
		
		if (node == first) {
			first = first.next;
			if (node == last) {
				last = null;
			}	size--;
				return;
			}
		// Otherwise, find the node's predecessor
        Node current = first;
        Node previous = null;

        while (current != null && current != node) {
            previous = current;
            current = current.next;
        }

        // If not found, do nothing
        if (current == null) {
            return;
        }

        // Bypass 'node'
        previous.next = current.next;

        // If 'node' was the last, update 'last'
        if (node == last) {
            last = previous;
        }
        size--;
    }
	

	/**
	 * Removes from this list the node which is located at the given index.
	 * 
	 * @param index the location of the node that has to be removed.
	 * @throws IllegalArgumentException
	 *         if index is negative or greater than or equal to size
	 */
	public void remove(int index) {

		if (index < 0) {
			throw new IllegalArgumentException("Index cannot be negative: " + index);
		}

		Node nodeToRemove = getNode(index);

		if (nodeToRemove == null) {
			throw new IllegalArgumentException("Index out of bounds: " + index);
		}

		remove(nodeToRemove);
	}

	/**
 * Removes from this list the node pointing to the given memory block.
 * 
 * @param block the memory block that should be removed from the list
 * @throws IllegalArgumentException if the given memory block is not in this list
 */
public void remove(MemoryBlock block) {
	int index = indexOf(block);
    if (index == -1) {
        throw new IllegalArgumentException("Block not found in this list");
    }
    remove(index);
}


	/**
	 * Returns an iterator over this list, starting with the first element.
	 */
	public ListIterator iterator(){
		return new ListIterator(first);
	}
	
	/**
	 * A textual representation of this list, for debugging.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		Node current = first;
		while (current != null) {
			sb.append(current.block);  // or current.block.toString()
			
			// If there's another node ahead, add a comma separator
			if (current.next != null) {
				sb.append(", ");
			}
			current = current.next;
		}
		
		sb.append("]");
		return sb.toString();
	}
}
