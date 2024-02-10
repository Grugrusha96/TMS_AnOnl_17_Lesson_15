package BoundedBuffer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer {
     private final Queue<Integer> buffer;
     private final int capaciti;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int capaciti){
        this.buffer = new LinkedList<>();
        this.capaciti = capaciti;
    }
    public void produce(int data) throws IllegalArgumentException{
        lock.lock();
        try {
            while (buffer.size() == capaciti){
                System.out.println("Буфер заполнен. Продюсер ждет...");
                notFull.await();
            }
            buffer.offer(data);
            System.out.println("Продюсер :" + data);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
    public void consume() throws InterruptedException{
        lock.lock();
        try {
            while (buffer.isEmpty()){
                System.out.println("Буфер пуст. Потребиьель ждет...");
                notEmpty.await();
            }
            int data = buffer.poll();
            System.out.println("Потребитель :" + data);
            notFull.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
