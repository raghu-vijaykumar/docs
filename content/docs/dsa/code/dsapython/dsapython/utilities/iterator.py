class Node:
    def __init__(self, data):
        """
        Initializes a node with the given data.

        :param data: The data to be stored in the node.
        """
        self.data = data  # Data stored in the node
        self.next = None  # Reference to the next node in the list


class LinkedList:
    def __init__(self):
        """
        Initializes an empty linked list.
        """
        self.head = None  # The head of the linked list

    def add(self, data):
        """
        Adds a new element to the end of the linked list.

        :param data: The data to be added to the list.
        """
        new_node = Node(data)  # Create a new node with the given data
        if not self.head:  # If the list is empty, set the new node as the head
            self.head = new_node
        else:
            current = self.head  # Start from the head of the list
            while current.next:  # Traverse to the end of the list
                current = current.next
            current.next = new_node  # Add the new node at the end of the list

    def __iter__(self):
        """
        Returns an iterator for the linked list.

        :return: An instance of LinkedListIterator.
        """
        return LinkedListIterator(self.head)  # Return an iterator starting at the head


class LinkedListIterator:
    def __init__(self, head):
        """
        Initializes the iterator with the starting head of the list.

        :param head: The head node of the linked list.
        """
        self.current = head  # The current node in the iteration

    def __iter__(self):
        """
        Returns the iterator instance itself.

        :return: The iterator instance.
        """
        return self

    def __next__(self):
        """
        Returns the next element in the iteration.

        :return: The data of the next node.
        :raises StopIteration: If there are no more elements.
        """
        if self.current is None:  # If there are no more elements, raise StopIteration
            raise StopIteration
        data = self.current.data  # Get the data from the current node
        self.current = self.current.next  # Move to the next node
        return data  # Return the data of the current node


if __name__ == "__main__":
    # Test the LinkedList class and its iterator
    ll = LinkedList()  # Create a new linked list
    ll.add(1)  # Add elements to the list
    ll.add(2)
    ll.add(3)

    # Iterate over the linked list and print each element
    for value in ll:
        print(value)
