import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LogicClock {
    private static final HashMap<Integer, Integer> times = new HashMap<>();

    public static HashMap<Integer, Integer> getAndIncrease(int id) {
        if (!times.containsKey(id))
            times.put(id, 0);
        times.put(id, times.get(id) + 1);
        System.out.println("LogicClock getAndIncrease " + id + ", " + times.get(id));
        return new HashMap<>(times);
    }

    public static HashMap<Integer, Integer> setToMax(HashMap<Integer, Integer> i) {
        for (int j : i.keySet()) {
            if (!times.containsKey(j))
                times.put(j, 0);
            times.put(j, Math.max(times.get(j), i.get(j)));
        }
        return times;
    }

    private static boolean notHappenedBefore(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        Set<Integer> intersection = new HashSet<>(h1.keySet());
        intersection.retainAll(h2.keySet());
        for (int i : intersection)
            if (h1.get(i) > h2.get(i)) {
                System.out.println("!(" + h1 + " happened before " + h2 + ")");
                return true;
            }
        return false;
    }

    static boolean notHappenedBefore(TextEvent mte1, TextEvent mte2) {
        return notHappenedBefore(mte1.getTimeStamp(), mte2.getTimeStamp());
    }
}
