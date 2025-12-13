public class LinkedStack<T> implements StackInterface<T> {
    private Node<T> top;

    public LinkedStack() {
        top = null;
    }

    @Override
    public void push(T newEntry) {
        Node<T> newNode = new Node<>(newEntry);
        newNode.next = top;
        top = newNode;
    }

    @Override
    public T pop() {
        if (isEmpty()) return null;
        T data = top.data;
        top = top.next;
        return data;
    }

    @Override
    public T peek() {
        return isEmpty() ? null : top.data;
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }

    @Override
    public void clear() {
        top = null;
    }
}
