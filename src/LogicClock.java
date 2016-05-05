public class LogicClock {
    private static int time = 0;

    public static int getAndIncrease() {
        int res = time;
        time++;
        return res;
    }

    public static int setToMax(int i){
        time = Math.max(time, i) + 1;
        return time;
    }

}
