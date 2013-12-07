# Copyright (C) 2003-2008 Avery J. Regier.
#
# This file is part of the Tournament Pool and Bracket Tracker.
#
# Tournament Pool and Bracket Tracker is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# Tournament Pool and Bracket Tracker is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# This script upgrades from version 0.5.2 to 0.6.0.

USE TOURNAMENT;

CREATE TABLE TEAM_SYNONYM (
	TEAM_ID INTEGER NOT NULL,
	NAME VARCHAR(70) NOT NULL,
	FOREIGN KEY(TEAM_ID) REFERENCES TEAM(TEAM_ID)
);

ALTER TABLE GAME MODIFY COLUMN START_TIME TIMESTAMP;

CREATE TABLE GAME_SCORE (
	GAME_NODE_ID INTEGER NOT NULL,
	TOURNAMENT_ID INTEGER NOT NULL,
	OPPONENT_ID INTEGER NOT NULL,
	SCORE INTEGER NOT NULL,
	UNIQUE(GAME_NODE_ID, TOURNAMENT_ID, OPPONENT_ID),
	FOREIGN KEY(GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID),
	FOREIGN KEY(OPPONENT_ID) REFERENCES OPPONENT(OPPONENT_ID)
);

