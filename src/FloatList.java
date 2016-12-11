/** Growable array of floats. */

public class FloatList {
  private static final int DEFAULT_SIZE = 10;

  private float[] data = new float[DEFAULT_SIZE];
  private int numElements;

  public void add(float f) {
    if (numElements == data.length) {
      resize(1 + numElements);
    }
    data[numElements++] = f;
    assert numElements <= data.length;
  }

  public int size() {
    return numElements;
  }

  public float get(int index) {
    if (index >= numElements) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    return data[index];
  }

  public void put(int index, float val) {
    if (index >= numElements) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    data[index] = val;
  }

  public void trim() {
    if (data.length > numElements) {
      float[] newData = new float[numElements];
      System.arraycopy(data, 0, newData, 0, numElements);
      data = newData;
    }
  }

  public float[] getData() {
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
    float[] newData = new float[newCapacity];
    System.arraycopy(data, 0, newData, 0, data.length);
    data = newData;
  }
}