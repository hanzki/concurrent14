import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Countdown {

    private int value;

    public Countdown(int n) {
        this.value = n;
    }

    public void awaitZero() {
        synchronized(this) {
            while(value > 0) {
                try {
                    this.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void countdown() {
        value--;
        this.notify();
    }

    public static void main(String[] args) throws Exception{
        final Countdown cd = new Countdown(10);
        ExecutorService es = Executors.newFixedThreadPool(4);
        es.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Staring the wait");
                cd.awaitZero();
                System.out.println("Waiting done");
            }
        });
        for (int i = 0 ; i < 10; ++i){
            es.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("counting down.");
                    cd.countdown();
                }
            });
        }
        es.shutdown();
        if(es.awaitTermination(10000L, TimeUnit.MILLISECONDS)){
            System.out.println("done");
            System.exit(0);
        } else {
            System.out.println("error");
            System.exit(1);
        }
    }
}
