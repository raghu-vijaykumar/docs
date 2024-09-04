import pytest
from dsapython.utilities.iterator import (
    LinkedListIterator,
    Node,
)  # Assuming the classes are in linked_list.py


def test_linked_list_iterator_initialization():
    # Test that the iterator is initialized correctly with a valid head
    node1 = Node(10)
    iterator = LinkedListIterator(node1)
    assert iterator.current == node1


def test_linked_list_iterator_empty():
    # Test that the iterator raises StopIteration immediately if initialized with an empty list
    iterator = LinkedListIterator(None)

    with pytest.raises(StopIteration):
        next(iterator)


def test_linked_list_iterator_single_element():
    # Test iteration through a single element
    node1 = Node(10)
    iterator = LinkedListIterator(node1)

    assert next(iterator) == 10

    with pytest.raises(StopIteration):
        next(iterator)


def test_linked_list_iterator_multiple_elements():
    # Test iteration through multiple elements
    node1 = Node(10)
    node2 = Node(20)
    node3 = Node(30)

    node1.next = node2
    node2.next = node3

    iterator = LinkedListIterator(node1)

    assert next(iterator) == 10
    assert next(iterator) == 20
    assert next(iterator) == 30

    with pytest.raises(StopIteration):
        next(iterator)


def test_linked_list_iterator_restarts_each_time():
    # Ensure that each new iterator restarts from the beginning of the list
    node1 = Node(10)
    node2 = Node(20)
    node1.next = node2

    iterator1 = LinkedListIterator(node1)
    assert next(iterator1) == 10

    iterator2 = LinkedListIterator(node1)
    assert next(iterator2) == 10
    assert next(iterator2) == 20

    with pytest.raises(StopIteration):
        next(iterator2)
