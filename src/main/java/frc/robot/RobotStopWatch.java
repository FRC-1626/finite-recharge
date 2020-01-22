package frc.robot;

public class RobotStopWatch {
    String watchName;
    long startMs;

    public RobotStopWatch(String wn) {
        watchName = wn;
        startMs = System.currentTimeMillis();
    }

    public String toString() {
        long endMs = System.currentTimeMillis();

        return String.format("%s: %.3f", watchName, ((double)(endMs - startMs))/1000.0);
    }
}