public class WaitSilentlyForever extends Thread {

    @Override
    public void run() {
        synchronized(this) {
            while(true) {
                if(true) {
                    try {
                        this.wait();
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.err.println("An error has occurred!");
            }
        }
    }

    public static void main(String[] args) {
        WaitSilentlyForever s = new WaitSilentlyForever();
        s.start();

        //The programmer intends to that the program wait silently forever
        //Is he correct? Refer to the Java Language Specification chapter 17.2
    }
}
