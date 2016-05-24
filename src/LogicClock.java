import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("Convert2Diamond")
class LogicClock {
    private static final HashMap<Integer, Integer> times = new HashMap<Integer, Integer>();

    static HashMap<Integer, Integer> getAndIncrease(int id) {
        if (!times.containsKey(id))
            times.put(id, 0);
        times.put(id, times.get(id) + 1);
        System.out.println("LogicClock getAndIncrease " + id + ", " + times.get(id));
        return new HashMap<Integer, Integer>(times);
    }

    @SuppressWarnings("UnusedReturnValue")
    static HashMap<Integer, Integer> setToMax(HashMap<Integer, Integer> i) {
        for (int j : i.keySet()) {
            if (!times.containsKey(j))
                times.put(j, 0);
            times.put(j, Math.max(times.get(j), i.get(j)));
        }
        return times;
    }

    private static boolean notHappenedBefore(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        Set<Integer> intersection = new HashSet<Integer>(h1.keySet());
        intersection.retainAll(h2.keySet());
        for (int i : intersection)
            if (h1.get(i) > h2.get(i)) {
                System.out.println("!(" + h1 + " happened before " + h2 + ")");
                return true;
            }
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean isConcurrent(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        return notHappenedBefore(h1, h2) && notHappenedBefore(h2, h1);
    }

    static boolean notHappenedBefore(TextEvent mte1, TextEvent mte2) {
        return notHappenedBefore(mte1.getTimeStamp(), mte2.getTimeStamp());
    }
}
