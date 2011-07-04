package org.passwordmaker.android;

public class IntArrayList {
	int[] data;
	int capacity;
	int size = 0;
	
	public IntArrayList(int capacity) {
		this.capacity = capacity;
		data = new int[capacity];
	}
	
	public IntArrayList() {
		data = null;
		capacity = 0;
	}
	
	public IntArrayList(int[] data) {
		this.data = data;
		this.size = data.length;
		this.capacity = data.length;
	}
	
	public int length() {
		return size;
	}
	
	public int capacity() {
		return capacity;
	}
	
	public void setCapacity(int newCapacity) {
		int iterateTo = Math.min(newCapacity, size);
		int[] tmp = new int[newCapacity];
		for ( int i = 0; i < iterateTo; ++i ) {
			tmp[i] = data[i];
		}
		capacity = newCapacity;
		data = tmp;
	}
	
	public int get(int index) {
		return data[index];
	}
	
	public void put(int index, int value) {
		assert index < size;
		data[index] = value;
	}
	
	public void add(int value) {
		if ( size >= capacity ) {
			while (size >= capacity ) {
				capacity *= 2; 
			}
			setCapacity(capacity);
		}
		data[size++] = value;
	}
	
	public int[] toArray() {
		int[] result = new int[size];
		for ( int i = 0; i < size; ++i ) {
			result[i] = data[i];
		}
		return result;
	}
}
