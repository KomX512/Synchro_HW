
import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        final String LETTERS_SET = "RLRFR";
        final int ATTEMTS = 1000;
        final int GENERATION_LENGHT = 100;

        List<Thread> threadsList = new ArrayList<>();

        for (int i = 0; i < ATTEMTS; i++) {

            Thread thread = new Thread(() -> {
                String charsLine = generateRoute(LETTERS_SET, GENERATION_LENGHT);
                int rCount = charCount(charsLine);
                synchronized (sizeToFreq) {
                    sizeToFreq.put(rCount, sizeToFreq.getOrDefault(rCount, 0) + 1);
                }
            });

            threadsList.add(thread);
            thread.start();
        }

        for (Thread current : threadsList){
            current.join();
        }

        int maxKey = sizeToFreq.keySet().stream().max(Integer::compare).get();
        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", maxKey, sizeToFreq.get(maxKey));
        sizeToFreq.entrySet()
                .stream()
                .filter(current -> !current.getKey().equals(maxKey))
                .sorted(Map.Entry.<Integer, Integer>comparingByKey().reversed())
                .forEach(current -> System.out.printf(" - %d (%d раз) \n", current.getKey(), current.getValue()));
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