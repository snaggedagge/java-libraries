package dkarlsso.commons.raspberry;

import java.time.Duration;
import java.time.Instant;

public class RunningClock {

    private boolean isRunning = false;

    private final Instant programStarted = Instant.now();

    private Instant runningSince = Instant.now();

    private Duration totalRunningSinceReset = Duration.ofHours(0);

    public void setStartedRunning(boolean isRunning) {
        if(this.isRunning) {
            totalRunningSinceReset = Duration.ofNanos(Duration.between(runningSince, Instant.now()).toNanos()
                    + totalRunningSinceReset.toNanos());
        }
        runningSince = Instant.now();
        this.isRunning = isRunning;
    }

    public Duration getRunningTimeAndReset() {
        final Duration runningTime = totalRunningSinceReset;
        totalRunningSinceReset = Duration.ofHours(0);
        return runningTime;
    }

    public Duration getTotalRunningTime() {
        return Duration.between(programStarted, Instant.now());
    }

}
