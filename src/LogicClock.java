import java.util.HashMap;

public class LogicClock {
    private static HashMap<Integer, Integer> times = new HashMap<Integer, Integer>();

    public static HashMap<Integer, Integer> getAndIncrease(int id) {
        if (!times.containsKey(id))
            times.put(id, 0);
        times.put(id, times.get(id) + 1);
        System.out.println("LC GAI " + id + ", " + times.get(id));
        return new HashMap<Integer, Integer>(times);
    }

    public static HashMap<Integer, Integer> setToMax(HashMap<Integer, Integer> i) {
        System.out.println("LC STM");
        for (int j : i.keySet()) {
            if (!times.containsKey(j))
                times.put(j, 0);
            times.put(j, Math.max(times.get(j), i.get(j)));
        }
        return times;
    }

    public static boolean happenedBefore(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        System.out.println("LC HB");
        for (int i : h1.keySet())
            if (h1.get(i) > h2.get(i)) {
                System.out.println(h1 + " happened before " + h2);
                return false;
            }
        return true;
    }

    public static boolean isConcurrent(HashMap<Integer, Integer> h1, HashMap<Integer, Integer> h2) {
        return !happenedBefore(h1, h2) && !happenedBefore(h2, h1);
    }

    public static boolean happenedBefore(MyTextEvent mte1, MyTextEvent mte2) {
        return happenedBefore(mte1.getTimeStamp(), mte2.getTimeStamp());
    }
}
