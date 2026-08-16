package aston.task1;

import java.util.Objects;

public final class HashMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        private Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private final float loadFactor;

    public HashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0");
        }

        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("loadFactor must be positive and not NaN");
        }

        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    public int size() {
        return size;
    }

    public V get(Object key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    public V put(K key, V value) {
        if (table == null) {
            resize();
        }

        int h = hash(key);
        int i = index(h, table.length);
        Node<K, V> node = table[i];

        if (node == null) {
            table[i] = new Node<>(h, key, value, null);
        } else {
            while (true) {
                if (node.hash == h && Objects.equals(key, node.key)) {
                    V oldValue = node.value;
                    node.value = value;
                    return oldValue;
                }

                if (node.next == null) {
                    node.next = new Node<>(h, key, value, null);
                    break;
                }

                node = node.next;
            }
        }

        if (++size > threshold) {
            resize();
        }

        return null;
    }

    public V remove(Object key) {
        Node<K, V>[] currentTable = table;

        if (currentTable == null) {
            return null;
        }

        int h = hash(key);
        int i = index(h, currentTable.length);

        Node<K, V> previous = null;
        Node<K, V> current = currentTable[i];

        while (current != null) {
            if (current.hash == h && Objects.equals(key, current.key)) {
                V oldValue = current.value;

                if (previous == null) {
                    currentTable[i] = current.next;
                } else {
                    previous.next = current.next;
                }

                size--;
                return oldValue;
            }

            previous = current;
            current = current.next;
        }

        return null;
    }

    private Node<K, V> findNode(Object key) {
        Node<K, V>[] currentTable = table;

        if (currentTable == null) {
            return null;
        }

        int h = hash(key);
        Node<K, V> current = currentTable[index(h, currentTable.length)];

        while (current != null) {
            if (current.hash == h && Objects.equals(key, current.key)) {
                return current;
            }

            current = current.next;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int oldCapacity = oldTable == null ? 0 : oldTable.length;

        int newCapacity;
        int newThreshold;

        if (oldCapacity > 0) {
            if (oldCapacity >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return;
            }

            newCapacity = oldCapacity << 1;
            newThreshold = (int) (newCapacity * loadFactor);
        } else {
            newCapacity = threshold;

            if (newCapacity <= 0) {
                newCapacity = 1;
            }

            newThreshold = (int) (newCapacity * loadFactor);
        }

        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];

        if (oldCapacity > 0) {
            for (int i = 0; i < oldCapacity; i++) {
                Node<K, V> loHead = null;
                Node<K, V> loTail = null;
                Node<K, V> hiHead = null;
                Node<K, V> hiTail = null;

                Node<K, V> current = oldTable[i];

                while (current != null) {
                    Node<K, V> next = current.next;

                    if ((current.hash & oldCapacity) == 0) {
                        if (loTail == null) {
                            loHead = current;
                        } else {
                            loTail.next = current;
                        }

                        loTail = current;
                    } else {
                        if (hiTail == null) {
                            hiHead = current;
                        } else {
                            hiTail.next = current;
                        }

                        hiTail = current;
                    }

                    current = next;
                }

                if (loHead != null) {
                    loTail.next = null;
                    newTable[i] = loHead;
                }

                if (hiHead != null) {
                    hiTail.next = null;
                    newTable[i + oldCapacity] = hiHead;
                }
            }
        }

        table = newTable;
        threshold = newThreshold;
    }

    private static int hash(Object key) {
        if (key == null) {
            return 0;
        }

        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private static int index(int hash, int length) {
        return hash & (length - 1);
    }

    private static int tableSizeFor(int capacity) {
        int n = capacity - 1;

        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;

        if (n < 0) {
            return 1;
        }

        if (n >= MAXIMUM_CAPACITY) {
            return MAXIMUM_CAPACITY;
        }

        return n + 1;
    }
}