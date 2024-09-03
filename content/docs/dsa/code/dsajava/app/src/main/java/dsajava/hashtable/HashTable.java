package dsajava.hashtable;

public class HashTable {

    int length = 7;
    Node[] dataMap;

    public class Node {
        String key;
        int value;
        Node next;

        public Node(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public HashTable() {
        dataMap = new Node[length];
    }

    private int hash(String key) {
        int hash = 0;
        char[] keyChars = key.toCharArray();
        for (int i = 0; i < keyChars.length; i++) {
            hash += (keyChars[i] * 37) % dataMap.length;
        }
        return hash;
    }

    public void put(String key, int value) {
        int hash = hash(key);
        Node newNode = new Node(key, value);
        if (dataMap[hash] == null) {
            dataMap[hash] = newNode;
        }
        Node temp = dataMap[hash];
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
    }

    public int get(String key) {
        int hash = hash(key);
        Node temp = dataMap[hash];
        while (temp != null) {
            if (key == temp.key) {
                return temp.value;
            }
            temp = temp.next;
        }
        return 0;
    }

}
