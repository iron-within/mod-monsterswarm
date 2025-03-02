package com.ironwithin.monsterswarm.data;

import java.lang.reflect.Array;

public class ObjPool<T> {
    private final Class<T> clazz;
    private T[] stack;
    private int size;

    @SuppressWarnings("unchecked")
    public ObjPool(Class<T> clazz) {
        this.clazz = clazz;
        this.stack = (T[]) Array.newInstance(clazz, 128);
    }

    public T take() {
        if (this.size > 0) {
            return this.stack[--this.size];
        }
        try {
            return this.clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a new instance of " + clazz.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void ensureSize(int size) {
        if (this.stack.length < size) {
            int newSize = Math.max((int) (this.stack.length * 1.4F), this.stack.length + 10);
            T[] newStack = (T[]) Array.newInstance(this.clazz, newSize);
            System.arraycopy(this.stack, 0, newStack, 0, this.size);
            this.stack = newStack;
        }
    }

    public void give(T obj) {
        ensureSize(this.size + 1);
        this.stack[this.size++] = obj;
    }
}