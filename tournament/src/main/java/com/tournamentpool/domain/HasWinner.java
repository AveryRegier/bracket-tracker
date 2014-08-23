package com.tournamentpool.domain;

import java.util.Optional;

/**
 * Created by avery on 8/22/14.
 */
public interface HasWinner {
    Optional<Opponent> getWinner();
}
