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

# This script upgrades from version 0.4.0 to 0.5.0.

USE TOURNAMENT;

# Sweet 16 support
ALTER TABLE TOURNAMENT ADD COLUMN PARENT_TOURNAMENT_ID INTEGER;
ALTER TABLE TOURNAMENT ADD FOREIGN KEY(PARENT_TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID);

ALTER TABLE TOURNAMENT ADD COLUMN START_LEVEL_ID INTEGER;
ALTER TABLE TOURNAMENT ADD FOREIGN KEY(START_LEVEL_ID) REFERENCES LEVEL(LEVEL_ID);

ALTER TABLE TOURNAMENT MODIFY COLUMN TOURNAMENT_TYPE_ID INTEGER;
ALTER TABLE TOURNAMENT MODIFY COLUMN LEAGUE_ID INTEGER;

# Add email addresses in case players forget their passwords
ALTER TABLE PLAYER ADD COLUMN EMAIL CHAR(254);

# Allow group admins to set a limit on the number of brackets in the pool by an individual
ALTER TABLE POOL ADD COLUMN BRACKET_LIMIT INTEGER NOT NULL DEFAULT 0;
# Ability to not show brackets before the tournament starts
ALTER TABLE POOL ADD COLUMN SHOW_EARLY CHAR(1) NOT NULL DEFAULT 'Y';

# Tie Breakers
CREATE TABLE TIE_BREAKER_TYPE (
	TIE_BREAKER_TYPE_ID INTEGER NOT NULL PRIMARY KEY,
	NAME VARCHAR(70)
);

INSERT INTO TIE_BREAKER_TYPE (TIE_BREAKER_TYPE_ID, NAME) VALUES (0, 'None');
INSERT INTO TIE_BREAKER_TYPE (TIE_BREAKER_TYPE_ID, NAME) VALUES (1, 'Closest Number');

ALTER TABLE POOL ADD COLUMN TIE_BREAKER_TYPE_ID INTEGER NOT NULL DEFAULT 0;
ALTER TABLE POOL ADD COLUMN TIE_BREAKER_QUESTION VARCHAR(254);
ALTER TABLE POOL ADD COLUMN TIE_BREAKER_ANSWER CHAR(30);
ALTER TABLE POOL ADD FOREIGN KEY(TIE_BREAKER_TYPE_ID) REFERENCES TIE_BREAKER_TYPE(TIE_BREAKER_TYPE_ID);

ALTER TABLE BRACKET_POOL ADD COLUMN TIE_BREAKER_ANSWER CHAR(30);
