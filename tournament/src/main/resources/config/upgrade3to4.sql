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

# This script upgrades from version 0.3.0 to 0.4.0.

USE TOURNAMENT;

CREATE TABLE LEAGUE (
	LEAGUE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL
);

CREATE TABLE LEAGUE_TEAM (
	LEAGUE_ID INTEGER NOT NULL,
	TEAM_ID INTEGER NOT NULL,
	UNIQUE(LEAGUE_ID, TEAM_ID),
	FOREIGN KEY(LEAGUE_ID) REFERENCES LEAGUE(LEAGUE_ID),
	FOREIGN KEY(TEAM_ID) REFERENCES TEAM(TEAM_ID)
);

ALTER TABLE TOURNAMENT ADD COLUMN LEAGUE_ID INTEGER;
ALTER TABLE TOURNAMENT ADD FOREIGN KEY(LEAGUE_ID) REFERENCES LEAGUE(LEAGUE_ID);

ALTER TABLE PLAYER ADD COLUMN SITE_ADMIN CHAR(1);
UPDATE PLAYER SET SITE_ADMIN='Y' WHERE PLAYER_ID=1;

# include fix from upgradeTo4Fix.sql
CREATE TABLE GAME_FEEDER_TYPE (
	GAME_FEEDER_TYPE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70)
);

ALTER TABLE GAME_NODE ADD COLUMN GAME_FEEDER_TYPE_ID INTEGER NOT NULL DEFAULT 1;
ALTER TABLE GAME_NODE ADD FOREIGN KEY(GAME_FEEDER_TYPE_ID) REFERENCES GAME_FEEDER_TYPE(GAME_FEEDER_TYPE_ID);

INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Normal');
INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Lowest Seed');
INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Highest Seed');
