/* 
Copyright (C) 2003-20013 Avery J. Regier.

This file is part of the Tournament Pool and Bracket Tracker.

This interface is intended as a plug-in point.
This interface may be implemented and linked with the Tournament Pool and Bracket Tracker
in individual installations without redistributing the source of the implementing class.
 */


package com.tournamentpool.controller.autoupdate;

import java.util.Date;
import java.util.Map;

public interface LiveGame {

	Map<String, Integer> getPlayerScores();

	String getGameID();

	boolean isFinal();

	String getWinner();

	String getStatus();

	Date getStartDate();

}
