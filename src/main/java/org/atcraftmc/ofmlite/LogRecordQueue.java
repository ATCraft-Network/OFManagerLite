package org.atcraftmc.ofmlite;

import java.util.ArrayList;
import java.util.List;

public final class LogRecordQueue {
    private final String[] queue;
    private int head = 0; // 队列头指针
    private int size = 0; // 当前队列大小
    private final int capacity; // 队列最大容量

    public LogRecordQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.queue = new String[capacity];
    }

    // 添加元素
    public void add(String value) {
        if (size == capacity) { // 队列已满，覆盖最老的元素
            queue[head] = value;
            head = (head + 1) % capacity; // 更新头指针
        } else { // 队列未满
            queue[(head + size) % capacity] = value;
            size++;
        }
    }

    // 获取队列头元素
    public String poll() {
        if (size == 0) {
            throw new IllegalStateException("Queue is empty");
        }
        String value = queue[head];
        head = (head + 1) % capacity; // 更新头指针
        size--;
        return value;
    }

    // 查看队列头元素（不移除）
    public String peek() {
        if (size == 0) {
            throw new IllegalStateException("Queue is empty");
        }
        return queue[head];
    }

    // 当前队列大小
    public int size() {
        return size;
    }

    // 判断队列是否为空
    public boolean isEmpty() {
        return size == 0;
    }

    public List<String> getContents() {
        List<String> contents = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            contents.add(queue[(head + i) % capacity]);
        }
        return contents;
    }
}