+++
title= "Iterator Pattern"
tags = [ "system-design",  "design-patterns", "iterator" ]
author = "Me"
date = 2024-08-31T00:01:00+05:30
showToc = true
TocOpen = false
draft = false
hidemeta = false
comments = false
disableShare = false
disableHLJS = false
hideSummary = false
searchHidden = true
ShowReadingTime = true
ShowBreadCrumbs = true
ShowPostNavLinks = true
ShowWordCount = true
ShowRssButtonInSectionTermList = true
UseHugoToc = true
weight = 1
bookFlatSection= true
+++

Iterator Design Pattern in Python
Overview
The iterator design pattern is a widely used pattern that separates the traversal logic of a data structure from its representation. The pattern allows for the creation of objects that can traverse a data structure without exposing the underlying details. This pattern is particularly helpful when dealing with complex data structures like trees, where traversal is not as simple as iterating through a list.

Iterator Protocol
In Python, the iterator protocol is simple and consists of two main components:

__iter__(): This method should return the iterator object.
__next__(): This method should return the next item from the iterator. When no more items are left, it should raise a StopIteration exception.
The key advantage of using iterators is that they abstract the details of traversal and allow for lazy evaluation of the data structure elements.

Example: In-Order Traversal of a Binary Tree
Consider a binary tree, where we want to traverse the nodes in different orders such as in-order, pre-order, and post-order. In this documentation, we'll focus on in-order traversal using both a stateful iterator and the yield keyword.

Node Definition
python
Copy code
class Node:
    def __init__(self, value, left=None, right=None):
        self.value = value
        self.left = left
        self.right = right
        self.parent = None
        
        if left:
            self.left.parent = self
        if right:
            self.right.parent = self

    def __iter__(self):
        return InOrderIterator(self)
The Node class represents a single node in the tree. Each node has a value, and potentially left and right children. The __iter__() method is implemented to return an InOrderIterator, allowing the tree to be iterated.

In-Order Iterator
python
Copy code
class InOrderIterator:
    def __init__(self, root):
        self.root = self.current = root
        self.yielded_start = False
        while self.current.left:
            self.current = self.current.left

    def __next__(self):
        if not self.yielded_start:
            self.yielded_start = True
            return self.current
        
        if self.current.right:
            self.current = self.current.right
            while self.current.left:
                self.current = self.current.left
            return self.current
        else:
            p = self.current.parent
            while p and self.current == p.right:
                self.current = p
                p = p.parent
            self.current = p
            if self.current:
                return self.current
            else:
                raise StopIteration
This InOrderIterator is a stateful iterator that traverses a binary tree using in-order traversal. The __next__() method navigates through the tree by keeping track of the current node and its parent, adjusting the traversal logic accordingly.

Stateful Iterators vs. Generators
Stateful iterators like InOrderIterator maintain explicit state (such as the current node) and manually handle the traversal logic. However, they can become complex and harder to maintain.

An alternative approach is to use Python's yield keyword to define a generator that handles traversal implicitly. Generators are easier to implement, read, and maintain compared to stateful iterators.

Generator-Based In-Order Traversal
python
Copy code
def traverse_in_order(root):
    def traverse(current):
        if current.left:
            for left in traverse(current.left):
                yield left
        yield current
        if current.right:
            for right in traverse(current.right):
                yield right
    for node in traverse(root):
        yield node
In this approach, we recursively traverse the tree and yield each node when visited. This eliminates the need for explicit state management and results in cleaner code.

Usage
Here's how you can use both the iterator and generator to perform in-order traversal of a simple binary tree:

python
Copy code
if __name__ == '__main__':
    # Tree structure:
    #    1
    #   / \
    #  2   3

    root = Node(1, Node(2), Node(3))

    # Using the iterator:
    it = iter(root)
    print([next(it).value for _ in range(3)])  # Output: [2, 1, 3]

    # Using a loop with iterator:
    for x in root:
        print(x.value)  # Output: 2 1 3

    # Using the generator:
    for y in traverse_in_order(root):
        print(y.value)  # Output: 2 1 3
Summary
The iterator design pattern helps in separating traversal logic from the structure of the data, making it easier to maintain and extend complex data structures. The InOrderIterator class is an example of a stateful iterator, while the traverse_in_order function demonstrates the use of Python generators for a more concise and readable implementation.

Both methods achieve the same goal, but using yield is often simpler and avoids the pitfalls of manually managing state within the iterator.

Array-backed Properties and Iteration in Python
Overview
This document explains a flexible approach to managing attributes and performing operations such as summing, averaging, and finding the maximum of these attributes for a class in Python. The goal is to address issues related to repetitive code and hardcoding attribute calculations by using array-backed properties and a custom iterator for tree traversal.

Refactoring to Array-backed Properties
Suppose you're designing a game where creatures have attributes like strength, agility, and intelligence. You want to calculate aggregate statistics such as the sum, maximum, and average of these attributes. A naive approach involves manually writing out these calculations, but this can lead to code that's difficult to maintain, especially when more attributes are added.

To avoid these pitfalls, we can refactor the code to use array-backed properties. This involves storing the attributes in a list and accessing them via properties, allowing for more flexible and maintainable code.

Initial Approach: Manual Attribute Handling
Here's the basic idea of how attributes were handled initially:

python
Copy code
class Creature:
    def __init__(self):
        self.strength = 10
        self.agility = 10
        self.intelligence = 10

    @property
    def sum_of_stats(self):
        return self.strength + self.agility + self.intelligence

    @property
    def max_stat(self):
        return max(self.strength, self.agility, self.intelligence)

    @property
    def average_stat(self):
        return self.sum_of_stats / 3.0
The key problems with this approach:

Hardcoding: Each attribute is manually typed out, making it easy to forget one or create errors when adding new attributes.
Magic Numbers: Constants like 3.0 (the number of attributes) need to be manually updated if more attributes are added, which can lead to bugs.
Improved Approach: Array-backed Properties
We can improve this by storing the attributes in a list and accessing them using properties. This eliminates the need for hardcoding and makes the code more maintainable.

python
Copy code
class Creature:
    _strength = 0
    _agility = 1
    _intelligence = 2

    def __init__(self):
        self.stats = [10, 10, 10]

    @property
    def strength(self):
        return self.stats[self._strength]

    @strength.setter
    def strength(self, value):
        self.stats[self._strength] = value

    @property
    def agility(self):
        return self.stats[self._agility]

    @agility.setter
    def agility(self, value):
        self.stats[self._agility] = value

    @property
    def intelligence(self):
        return self.stats[self._intelligence]

    @intelligence.setter
    def intelligence(self, value):
        self.stats[self._intelligence] = value

    @property
    def sum_of_stats(self):
        return sum(self.stats)

    @property
    def max_stat(self):
        return max(self.stats)

    @property
    def average_stat(self):
        return sum(self.stats) / len(self.stats)
Benefits of Array-backed Properties
Scalability: You can easily add more attributes without modifying existing logic. The aggregate calculations (e.g., sum, max, average) will automatically adjust.
Maintainability: You eliminate magic numbers like 3.0, ensuring that your calculations are always correct if attributes are added or removed.
Flexibility: Grouping related properties into lists allows you to iterate over them and perform calculations more easily.