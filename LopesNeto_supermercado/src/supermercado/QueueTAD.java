package supermercado;

public interface QueueTAD<E>
{
    void add(E element);
    E remove();
    int size();
    boolean isEmpty();
    void clear();
    E element();
}
