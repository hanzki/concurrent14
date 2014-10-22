package hangman;

import reactor.Dispatcher;
import reactorapi.EventHandler;
import reactorapi.Handle;
import reactorexample.AcceptHandle;
import reactorexample.TCPTextHandle;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by Hanzki on 22.10.2014.
 */
public class HangmanServer {
    private static final Logger log = Logger.getLogger( HangmanServer.class.getName());
    private final Dispatcher dispatcher;
    private final int guessLimit;
    private final String wordToGuess;

    private char[] currentWord;
    private int guessLeft;

    public HangmanServer(int guessLimit, String wordToGuess) throws IOException {
        this.guessLimit = guessLimit;
        this.wordToGuess = wordToGuess;
        dispatcher = new Dispatcher();

        guessLeft = guessLimit;
        currentWord = new char[wordToGuess.length()];
        Arrays.fill(currentWord, '-');

        dispatcher.addHandler(new AcceptHandler());
        log.info("Hangman server created");
    }

    public void execute() {
        System.out.println(wordToGuess + " " + guessLimit);
        try {
            dispatcher.handleEvents();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Shutting down");
        }
    }

    private class AcceptHandler implements EventHandler<Socket> {
        AcceptHandle ah;

        private AcceptHandler() throws IOException {
            ah = new AcceptHandle();
        }

        @Override
        public Handle<Socket> getHandle() {
            return ah;
        }

        @Override
        public void handleEvent(Socket s) {

            if (s == null) {
                dispatcher.removeHandler(this);
            } else {
                dispatcher.addHandler(new TCPHandler(s));
            }
        }
    }

    private class TCPHandler implements EventHandler<String> {
        final TCPTextHandle th;
        final Socket socket;

        private TCPHandler(Socket s) {
            socket = s;
            th = new TCPTextHandle(s);
            log.info("New player " + System.identityHashCode(socket) + " joined ip:" + socket.getInetAddress().getHostAddress());
            th.write(String.valueOf(currentWord) + " " + guessLeft);
        }

        @Override
        public Handle<String> getHandle() {
            return th;
        }

        @Override
        public void handleEvent(String s) {
            if (s == null) {
                dispatcher.removeHandler(this);
                log.info("Player " + System.identityHashCode(socket) + " left.");
            } else {
                if(s.length() == 1 && 'a' <= s.charAt(0) && s.charAt(0) <= 'z'){
                    char guess = s.charAt(0);
                    if(wordToGuess.indexOf(guess) > -1){
                        int i = 0;
                        while((i = wordToGuess.indexOf(guess, i)) > -1){
                            currentWord[i] = wordToGuess.charAt(i);
                            i++;
                        }
                    } else {
                        guessLeft--;
                    }
                    th.write(guess + " " + String.valueOf(currentWord) + " " + guessLeft + " lol");
                }
                System.err.println(s);
                th.write(s);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid number of arguments!");
            printUsage();
            System.exit(1);
        }

        int guessLimit = 0;

        try {
            guessLimit = Integer.parseInt(args[1]);
            if (guessLimit < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            System.err.println("Invalid argument: Number for maximum number of guesses needs to be non-zero positive number");
            printUsage();
            System.exit(1);
        }

        String word = args[0];
        for (char c : word.toCharArray()) {
            if ('a' <= c && c <= 'z') continue;
            else {
                System.err.println("Invalid argument: Word to guess can only include lowercase letters (a-z)");
                printUsage();
                System.exit(1);
            }
        }

        try {
            HangmanServer server = new HangmanServer(guessLimit, word);
            server.execute();
        } catch (IOException ex) {
            System.err.println("Server start up failed.");
        }
    }

    static void printUsage() {
        System.out.println("Usage: HangmanServer <word to guess> <number of failed attempts before termination>");
    }

}
