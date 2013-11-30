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

# This script upgrades from version 0.2.x to 0.3.0.

USE TOURNAMENT;

CREATE TABLE TOURNAMENT_ADMIN (
	TOURNAMENT_ID INTEGER NOT NULL,
	ADMIN_ID INTEGER NOT NULL,
	UNIQUE(TOURNAMENT_ID, ADMIN_ID),
	FOREIGN KEY(ADMIN_ID) REFERENCES PLAYER(PLAYER_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID)
);

ALTER TABLE GROUP_NAME ADD COLUMN INVITATION_CODE INTEGER;
