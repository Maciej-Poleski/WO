package heap;

/**
 * Created by local_user on 2015-01-13.
 */
public class Main {
    public static void main(String[] args)
    {
        Heap heap=new Heap();
        heap.insert(5);
        heap.insert(15);
        heap.insert(10);
        System.out.println(heap.extractMin());
        System.out.println(heap.extractMin());
        System.out.println(heap.extractMin());
    }
}
