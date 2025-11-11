package nl.helicenter.flightmaster.utils;

public final class PatchUtil {
        public static <T> void applyIfPresent(T value, java.util.function.Consumer<T> setter) {
            if (value != null) setter.accept(value);
        }
    }
