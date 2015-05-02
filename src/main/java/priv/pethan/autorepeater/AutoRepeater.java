package priv.pethan.autorepeater;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AutoRepeater {
    public static <T> T perform(OptionalFunction<T> function) {
        Integer delayInSeconds = 1;
        Integer maxDelay = 128;
        Optional<T> result;

        do {
            result = function.apply();

            if (!result.isPresent()) {
                try {
                    TimeUnit.SECONDS.sleep(delayInSeconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (maxDelay < delayInSeconds) {
                    delayInSeconds = delayInSeconds * 2;
                }
            }
        } while (!result.isPresent());

        return result.get();
    }
}
