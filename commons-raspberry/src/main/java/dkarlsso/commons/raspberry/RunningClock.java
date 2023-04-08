package dkarlsso.commons.raspberry;

import java.time.Duration;
import java.time.Instant;

/**
 * A clock which measures how long something has actively been running
 */
public class RunningClock {

    private boolean isRunning = false;
    private Instant runningSince = Instant.now();

    private Duration durationSinceReset = Duration.ofHours(0);

    private Duration activeDurationSinceStart = Duration.ofHours(0);

    public void updateState(boolean isActive) {
        if(this.isRunning) {
            durationSinceReset = Duration.ofNanos(Duration.between(runningSince, Instant.now()).toNanos()
                    + durationSinceReset.toNanos());
            activeDurationSinceStart = Duration.ofNanos(Duration.between(runningSince, Instant.now()).toNanos()
                    + activeDurationSinceStart.toNanos());
        }
        runningSince = Instant.now();
        this.isRunning = isActive;
    }

    public Duration getDurationAndReset() {
        final Duration runningTime = durationSinceReset;
        durationSinceReset = Duration.ofHours(0);
        return runningTime;
    }

    public Duration getActiveDurationSinceStart() {
        return activeDurationSinceStart;
    }

}
