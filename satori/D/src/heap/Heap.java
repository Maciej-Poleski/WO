package heap;

import java.util.ArrayList;

public class Heap
{
    /**
     * An array of integers with heap property: element at index i
     * is never smaller than its parent (at index i/2); there is
     * nothing (null) stored at index 0.
     */
    private final ArrayList<Integer> data;

    public Heap()
    {
        data = new ArrayList<Integer>();
        data.add(null);
    }

    /**
     * Swaps the element at the given index with its parent.
     */
    private void swapWithParent(int index)
    {
        data.set(index, data.set(index/2, data.get(index)));
    }

    /**
     * Restores heap property between the element at the given index and its parent.
     */
    private boolean correctAt(int index)
    {
        if (data.get(index/2) <= data.get(index))
            return false;
        this.swapWithParent(index);
        return true;
    }

    /**
     * Restores heap property by moving the given element towards the root.
     */
    private void correctUpFrom(int index)
    {
        while (index > 1 && this.correctAt(index))
            index = index/2;
    }

    /**
     * Restores heap property by moving the given element towards the leaves.
     */
    private void correctDownFrom(int index)
    {
        while (true)
        {
            int child = index*2;
            if (child+1 < data.size() && data.get(child+1) < data.get(child))
                child = child+1;
            if (child >= data.size() || !this.correctAt(child))
                break;
            index = child;
        }
    }

    /**
     * Returns the number of elements stored in this Heap.
     */
    public int size()
    {
        return data.size() - 1;
    }

    /**
     * Returns whether this Heap is empty.
     */
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    /**
     * Inserts a new elements on this Heap.
     */
    public void insert(int value)
    {
        data.add(value);
        this.correctUpFrom(data.size() - 1);
    }

    /**
     * Returns the minimum value on the Heap.
     */
    public int getMin() throws IndexOutOfBoundsException
    {
        if (this.isEmpty())
            throw new IndexOutOfBoundsException();
        return data.get(1);
    }

    /**
     * Removes and returns the minimum value on the Heap.
     */
    public int extractMin() throws IndexOutOfBoundsException
    {
        switch (data.size())
        {
            case 1:
                throw new IndexOutOfBoundsException();
            case 2:
                return data.remove(1);
            default:
                int result = data.set(1, data.remove(data.size() - 1));
                this.correctDownFrom(1);
                return result;
        }
    }

    /**
     * Removes and returns (a sorted List of) all values on the Heap smaller that a given limit.
     */
    public int[] extractUntil(int limit)
    {
        ArrayList<Integer> boxed = new ArrayList<Integer>();
        while (!this.isEmpty() && this.getMin() < limit)
            boxed.add(this.extractMin());
        int[] result = new int[boxed.size()];
        int i = 0;
        for (Integer value : boxed)
            result[i++] = value;
        return result;
    }
}
