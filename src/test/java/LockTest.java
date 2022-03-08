import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {


    public static void main(String[] args) {


        ExecutorService service = Executors.newCachedThreadPool();
        work work = new work();
        service.execute(new demo(work));
        service.shutdown();

    }

}

class work{
    public synchronized void doSome(){
        try {
            this.wait(10000);
            System.out.println(Thread.currentThread().getName()+" work~~");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+" work");
    }
}

class demo implements Runnable{

    private work work;

    public demo(work work){
        this.work = work;
    }

    @Override
    public void run() {

        try {
            work.doSome();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }



    }
}
