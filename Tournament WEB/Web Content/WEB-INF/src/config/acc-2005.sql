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

SELECT @tournament_type_id:=TOURNAMENT_TYPE_ID FROM TOURNAMENT_TYPE WHERE NAME='11 Team';
SELECT @min_seed_id:=MIN(SEED_ID)-1 FROM SEED WHERE TOURNAMENT_TYPE_ID=@tournament_type_id;

INSERT INTO TOURNAMENT (NAME, TOURNAMENT_TYPE_ID)
VALUES ('2005 Men\s ACC Tournament', @tournament_type_id);

SELECT @tournament_id:=MAX(TOURNAMENT_ID) FROM TOURNAMENT;

#INSERT INTO TEAM (TEAM_ID, NAME) VALUES (66, 'Virginia Tech');
#INSERT INTO TEAM (TEAM_ID, NAME) VALUES (67, 'Clemson');
#INSERT INTO TEAM (TEAM_ID, NAME) VALUES (68, 'Florida State');
#INSERT INTO TEAM (TEAM_ID, NAME) VALUES (69, 'Virginia');

INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 38, @min_seed_id+1);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 20, @min_seed_id+2);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 33, @min_seed_id+3);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 66, @min_seed_id+4);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 3,  @min_seed_id+5);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 70, @min_seed_id+6);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 51, @min_seed_id+7);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 52, @min_seed_id+8);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 67, @min_seed_id+9);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 68, @min_seed_id+10);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 69, @min_seed_id+11);
