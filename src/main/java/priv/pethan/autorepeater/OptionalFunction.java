package priv.pethan.autorepeater;

import java.util.Optional;

public interface OptionalFunction<R> {
    Optional<R> apply();
}
