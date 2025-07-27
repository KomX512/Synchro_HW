
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        final String LETTERS_SET = "RLRFR";
        final int ATTEMTS = 1000;
        final int GENERATION_LENGHT = 100;

        List<Thread> threadsList = new ArrayList<>();
        ExecutorService threadPool = Executors.newCachedThreadPool();

        Thread observerThread = new Thread(() -> {
            int maxkey = 0;
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    int currentMaxKey = 0;
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        break;
                    }

                    currentMaxKey = sizeToFreq.keySet().stream().max(Integer::compare).get();

                    if (currentMaxKey > maxkey) {
                        maxkey = currentMaxKey;
                        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", maxkey, sizeToFreq.get(maxkey));
                    }
                }
            }
        });

        observerThread.start();

        for (int i = 0; i < ATTEMTS; i++) {

            Thread thread = new Thread(() -> {
                String charsLine = generateRoute(LETTERS_SET, GENERATION_LENGHT);
                int rCount = charCount(charsLine);
                synchronized (sizeToFreq) {
                    sizeToFreq.put(rCount, sizeToFreq.getOrDefault(rCount, 0) + 1);
                    sizeToFreq.notify();
                }
            });

            threadsList.add(thread);
            thread.start();
        }

        for (Thread current : threadsList) {
            current.join();
        }

        observerThread.interrupt();

//       int maxKey = sizeToFreq.keySet().stream().max(Integer::compare).get();
//        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", maxKey, sizeToFreq.get(maxKey));
//        sizeToFreq.entrySet()
//                .stream()
//                .filter(current -> !current.getKey().equals(maxKey))
//                .sorted(Map.Entry.<Integer, Integer>comparingByKey().reversed())
//                .forEach(current -> System.out.printf(" - %d (%d раз) \n", current.getKey(), current.getValue()));
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int charCount(String charsLine) {
        int rCount = 0;
        final char SEARCH_CHAR = 'R';

        for (int i = 0; i < charsLine.length(); i++) {
            if (charsLine.charAt(i) == SEARCH_CHAR) {
                rCount++;
            }
        }
        return rCount;
    }
}