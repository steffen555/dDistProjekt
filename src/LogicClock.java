import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LogicClock {
    private static HashMap<Integer, Integer> times = new HashMap<Integer, Integer>();

    public static HashMap<Integer, Integer> getAndIncrease(int id) {
        if (!times.containsKey(id))
            times.put(id, 0);
        times.put(id, times.get(id) + 1);
        System.out.println("LogicClock getAndIncrease " + id + ", " + times.get(id));
        return new HashMap<Integer, Integer>(times);
    }

    public static HashMap<Integer, Integer> setToMax(HashMap<Integer, Integer> i) {
        for (int j : i.keySet()) {
            if (!times.containsKey(j))
                times.put(j, 0);
            times.put(j, Math.max(times.get(j), i.get(j)));
        }
        return times;
    }

    public static boolean happenedBefore(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        Set<Integer> intersection = new HashSet<Integer>(h1.keySet());
        intersection.retainAll(h2.keySet());
        for (int i : intersection)
            if (h1.get(i) > h2.get(i)) {
                System.out.println("!(" + h1 + " happened before " + h2 + ")");
                return false;
            }
        return true;
    }

    public static boolean isConcurrent(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        return !happenedBefore(h1, h2) && !happenedBefore(h2, h1);
    }

    public static boolean happenedBefore(TextEvent mte1, TextEvent mte2) {
        return happenedBefore(mte1.getTimeStamp(), mte2.getTimeStamp());
    }
}
