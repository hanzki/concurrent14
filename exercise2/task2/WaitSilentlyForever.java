public class WaitSilentlyForever extends Thread {

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        WaitSilentlyForever s = new WaitSilentlyForever();
        s.start();
        s.interrupt();

        //The programmer intends to that the program wait silently forever
        //Is he correct? Refer to the Java Language Specification chapter 17.2
    }
}
