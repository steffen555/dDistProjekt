public class LogicClock {
    private int time;

    public LogicClock() {
        time = 0;
    }

    public int getAndIncrease() {
        int res = time;
        time++;
        return res;
    }

    public int setToMax(int i){
        time = Math.max(time, i) + 1;
        return time;
    }

}
