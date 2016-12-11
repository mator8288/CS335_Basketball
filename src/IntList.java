/** Growable array of ints. */

public class IntList {
  private static final int DEFAULT_SIZE = 10;

  private int[] data = new int[DEFAULT_SIZE];
  private int numElements;

  public void add(int f) {
    if (numElements == data.length) {
      resize(1 + numElements);
    }
    data[numElements++] = f;
    assert numElements <= data.length;
  }

  public int size() {
    return numElements;
  }

  public int get(int index) {
    if (index >= numElements) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    return data[index];
  }

  public void put(int index, int val) {
    if (index >= numElements) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    data[index] = val;
  }

  public void trim() {
    if (data.length > numElements) {
      int[] newData = new int[numElements];
      System.arraycopy(data, 0, newData, 0, numElements);
      data = newData;
    }
  }

  public int[] getData() {
    return data;
  }

  private void resize(int minCapacity) {
    int newCapacity = 2 * data.length;
    if (newCapacity == 0) {
      newCapacity = DEFAULT_SIZE;
    }
    if (newCapacity < minCapacity) {
      newCapacity = minCapacity;
    }
    int[] newData = new int[newCapacity];
    System.arraycopy(data, 0, newData, 0, data.length);
    data = newData;
  }
}