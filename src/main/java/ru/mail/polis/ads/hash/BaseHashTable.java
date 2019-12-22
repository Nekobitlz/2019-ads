package ru.mail.polis.ads.hash;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseHashTable<Key, Value> implements HashTable<Key, Value> {

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Entry<Key, Value>[] buckets = new Entry[INITIAL_CAPACITY];
    private int size = 0;
    private int capacity = INITIAL_CAPACITY;

    private static class Entry<Key, Value> {
        private Key key;
        private Value value;
        private Entry<Key, Value> next;

        private Entry(Key key, Value value, Entry<Key, Value> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @Nullable
    @Override
    public Value get(@NotNull Key key) {
        int hash = hash(key);
        Entry<Key, Value> entry = buckets[hash];

        while (entry != null && !entry.key.equals(key)) {
            entry = entry.next;
        }

        if (entry == null) return null;
        else return entry.value;
    }

    @Override
    public void put(@NotNull Key key, @NotNull Value value) {
        int hash = hash(key);
        Entry<Key, Value> entry = buckets[hash];

        if (entry == null) {
            buckets[hash] = new Entry<>(key, value, null);
            size++;
        } else {
            while (entry != null && !entry.key.equals(key)) {
                entry = entry.next;
            }

            if (entry != null) {
                entry.value = value;
                entry.next = new Entry<>(key, value, null);
            }
        }

        if (size > capacity * LOAD_FACTOR) {
            rehash();
        }
    }

    private void rehash() {
        capacity *= 2;
        size = 0;

        Entry<Key, Value>[] entries = buckets;
        buckets = new Entry[capacity];

        for (Entry<Key, Value> entry : entries) {
            Entry<Key, Value> head = entry;

            while (head != null) {
                put(head.key, head.value);
                head = head.next;
            }
        }
    }

    @Nullable
    @Override
    public Value remove(@NotNull Key key) {
        int hash = hash(key);

        Entry<Key, Value> prevEntry = null;
        Entry<Key, Value> entry = buckets[hash];

        if (entry != null) {
            Value entryValue = entry.value;
            size--;

            while (entry.next != null && !entry.key.equals(key)) {
                prevEntry = entry;
                entry = entry.next;
            }

            if (entry.key.equals(key)) {
                if (prevEntry == null) {
                    buckets[hash] = entry.next;
                    return entryValue;
                }
                else {
                    prevEntry.next = entry.next;
                    return entryValue;
                }
            }
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private int hash(Key key) {
        return Math.abs(key.hashCode() % buckets.length);
    }
}