package hangman;

import hangmanrules.HangmanRules;
import reactor.Dispatcher;
import reactorapi.EventHandler;
import reactorapi.Handle;
import reactorexample.AcceptHandle;
import reactorexample.TCPTextHandle;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Hanzki on 22.10.2014.
 */
public class HangmanServer {
    private static final Logger log = Logger.getLogger(HangmanServer.class.getName());
    private final Dispatcher dispatcher;

    private final List<TCPHandler> handlers;
    private final HangmanRules<TCPTextHandle> game;
    private final AcceptHandler ah;

    public HangmanServer(int guessLimit, String wordToGuess) throws IOException {
        dispatcher = new Dispatcher();

        handlers = new ArrayList<TCPHandler>();
        game = new HangmanRules<TCPTextHandle>(wordToGuess, guessLimit);

        ah = new AcceptHandler();
        dispatcher.addHandler(ah);
        log.info("Hangman server created");
    }

    public void execute() {
        try {
            dispatcher.handleEvents();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Shutting down");
        }
    }

    private void closeDispatcher() {
        log.info("closeDispatchers called");
        ah.ah.close();
        for (TCPHandler eh : handlers) {
            eh.th.close();
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
                ah.close();
            } else {
                TCPHandler h = new TCPHandler(s);
                dispatcher.addHandler(h);
                handlers.add(h);
            }
        }
    }

    private class TCPHandler implements EventHandler<String> {
        final TCPTextHandle th;
        final Socket socket;
        private HangmanRules.Player player;

        private TCPHandler(Socket s) {
            socket = s;
            th = new TCPTextHandle(s);
            player = null;
            th.write("Welcome to the Hangman Game. What's your name?");
            log.info("New player " + System.identityHashCode(socket) + " joined ip:" + socket.getInetAddress().getHostAddress());
        }

        @Override
        public Handle<String> getHandle() {
            return th;
        }

        @Override
        public void handleEvent(String s) {
            if (s == null) {
                game.removePlayer(player);
                handlers.remove(this);
                dispatcher.removeHandler(this);
                th.close();
                log.info("Player " + System.identityHashCode(socket) + " left.");
            } else if (player == null) {
                if (s.matches("[a-zA-Z]+")) {
                    player = game.addNewPlayer(th, s);
                    th.write(game.getStatus());
                }
            } else if (s.length() == 1 && 'a' <= s.charAt(0) && s.charAt(0) <= 'z') {
                char guess = s.charAt(0);
                game.makeGuess(guess);

                for (HangmanRules<TCPTextHandle>.Player p : game.getPlayers()) {
                    p.playerData.write(player.getGuessString(guess));
                }

                if (game.gameEnded()) {
                    closeDispatcher();
                }
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
            ex.printStackTrace();
            System.err.println("Server start up failed.");
        }
    }

    static void printUsage() {
        System.err.println("Usage: HangmanServer <word to guess> <number of failed attempts before termination>");
    }

}
