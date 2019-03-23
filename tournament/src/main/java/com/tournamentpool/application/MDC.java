/**
 * Copyright 2019 Deere & Company
 * <p>
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */

package com.tournamentpool.application;

import com.deere.isg.worktracker.OutstandingWork;
import com.deere.isg.worktracker.servlet.HttpWork;

import java.util.Optional;

public final class MDC {
    private static OutstandingWork<HttpWork> outstandingWork;

    private MDC() {}

    static OutstandingWork<HttpWork> init(OutstandingWork<HttpWork> ws) {
        outstandingWork = ws;
        return ws;
    }

    public static void put(String key, String value) {
        assert outstandingWork != null : "Call init to initialize outstandingWork";
        outstandingWork.putInContext(key, value);
    }

    public static Optional<HttpWork> getWork() {
        return outstandingWork.current();
    }
}