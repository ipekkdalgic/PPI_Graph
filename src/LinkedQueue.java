public class LinkedQueue<T> implements QueueInterface<T> {
    private Node<T> front;
    private Node<T> back;

    public LinkedQueue() {
        front = null;
        back = null;
    }

    @Override
    public void enqueue(T newEntry) {
        Node<T> newNode = new Node<>(newEntry);
        if (isEmpty()) {
            front = newNode;
            back = newNode;
        } else {
            back.next = newNode;
            back = newNode;
        }
    }

    @Override
    public T dequeue() {
        if (isEmpty()) return null;

        T data = front.data;
        front = front.next;
        if (front == null) back = null; // boşaldı
        return data;
    }

    @Override
    public T getFront() {
        return isEmpty() ? null : front.data;
    }

    @Override
    public boolean isEmpty() {
        return front == null;
    }

    @Override
    public void clear() {
        front = null;
        back = null;
    }
}
