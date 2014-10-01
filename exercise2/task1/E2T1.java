import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanzki on 24.9.2014.
 */
public class E2T1 implements Runnable{

    @Override
    public void run() {
        try {
            System.out.println(this + " taking a nap");
            Thread.sleep(500L);
            System.out.println(this + " is feeling refreshed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(2);
        Runnable A = new E2T1();
        Runnable B = new E2T1();
        es.execute(A);
        es.execute(B);
        es.shutdown();
        if(es.awaitTermination(5000, TimeUnit.MILLISECONDS)){
            System.out.println("done");
        } else {
            throw new AssertionError("Threads didn't terminate properly before the time limit");
        }

    }
}
