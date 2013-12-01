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

SELECT @tournament_type_id:=TOURNAMENT_TYPE_ID FROM TOURNAMENT_TYPE WHERE NAME='NCAA Tournament';
SELECT @min_seed_id:=MIN(SEED_ID)-1 FROM SEED WHERE TOURNAMENT_TYPE_ID=@tournament_type_id;

INSERT INTO TOURNAMENT (NAME, TOURNAMENT_TYPE_ID)
VALUES ('2004 Men\s NCAA Tournament', @tournament_type_id);

SELECT @tournament_id:=MAX(TOURNAMENT_ID) FROM TOURNAMENT;

INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 1, @min_seed_id+1);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 2, @min_seed_id+2);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 3, @min_seed_id+3);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 4, @min_seed_id+4);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 5, @min_seed_id+5);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 6, @min_seed_id+6);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 7, @min_seed_id+7);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 8, @min_seed_id+8);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 9, @min_seed_id+9);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 10, @min_seed_id+10);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 11, @min_seed_id+11);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 12, @min_seed_id+12);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 13, @min_seed_id+13);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 14, @min_seed_id+14);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 15, @min_seed_id+15);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 16, @min_seed_id+16);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 17, @min_seed_id+17);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 18, @min_seed_id+18);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 19, @min_seed_id+19);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 20, @min_seed_id+20);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 21, @min_seed_id+21);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 22, @min_seed_id+22);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 23, @min_seed_id+23);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 24, @min_seed_id+24);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 25, @min_seed_id+25);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 26, @min_seed_id+26);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 27, @min_seed_id+27);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 28, @min_seed_id+28);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 29, @min_seed_id+29);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 30, @min_seed_id+30);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 31, @min_seed_id+31);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 32, @min_seed_id+32);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 33, @min_seed_id+33);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 34, @min_seed_id+34);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 35, @min_seed_id+35);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 36, @min_seed_id+36);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 37, @min_seed_id+37);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 38, @min_seed_id+38);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 39, @min_seed_id+39);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 40, @min_seed_id+40);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 41, @min_seed_id+41);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 42, @min_seed_id+42);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 43, @min_seed_id+43);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 44, @min_seed_id+44);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 45, @min_seed_id+45);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 46, @min_seed_id+46);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 47, @min_seed_id+47);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 48, @min_seed_id+48);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 49, @min_seed_id+49);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 50, @min_seed_id+50);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 51, @min_seed_id+51);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 52, @min_seed_id+52);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 53, @min_seed_id+53);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 54, @min_seed_id+54);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 55, @min_seed_id+55);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 56, @min_seed_id+56);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 57, @min_seed_id+57);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 58, @min_seed_id+58);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 59, @min_seed_id+59);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 60, @min_seed_id+60);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 61, @min_seed_id+61);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 62, @min_seed_id+62);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 63, @min_seed_id+63);
INSERT INTO TOURNAMENT_SEED (TOURNAMENT_ID, TEAM_ID, SEED_ID) VALUES (@tournament_id, 64, @min_seed_id+64);
