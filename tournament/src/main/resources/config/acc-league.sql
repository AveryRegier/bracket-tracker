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

USE TOURNAMENT;

INSERT INTO LEAGUE(NAME) VALUES ('ACC Men\'s');
SELECT @league_id:=MAX(LEAGUE_ID) FROM LEAGUE;

INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 38);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 20);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 33);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 66);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 3);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 70);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 51);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 52);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 67);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 68);
INSERT INTO LEAGUE_TEAM(LEAGUE_ID, TEAM_ID) VALUES (@league_id, 69);
