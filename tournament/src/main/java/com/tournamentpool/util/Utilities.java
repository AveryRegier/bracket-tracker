package com.tournamentpool.util;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by avery on 3/24/17.
 */
public class Utilities {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static  <T> Stream<T> asStream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }
}
