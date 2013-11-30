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

SELECT @max_tournament_type_id:=IFNULL(MAX(TOURNAMENT_TYPE_ID), 0) FROM TOURNAMENT_TYPE;
SELECT @max_game_node_id:=IFNULL(MAX(GAME_NODE_ID), 0) FROM GAME_NODE;
SELECT @max_seed_id:=IFNULL(MAX(SEED_ID), 0) FROM SEED;

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (6);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (5);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (5);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (4);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (4);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (4);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (4);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);

INSERT INTO TOURNAMENT_TYPE (
	NAME, LAST_GAME_NODE_ID
) VALUES ('NCAA Tournament', @max_game_node_id+1);

INSERT INTO OPPONENT (
	TOURNAMENT_TYPE_ID, SEQUENCE, NAME
) VALUES (@max_tournament_type_id+1, 1, 'Home');

SELECT @home_id:=MAX(OPPONENT_ID) FROM OPPONENT;

INSERT INTO OPPONENT (
	TOURNAMENT_TYPE_ID, SEQUENCE, NAME
) VALUES (@max_tournament_type_id+1, 2, 'Visitor');

SELECT @visitor_id:=MAX(OPPONENT_ID) FROM OPPONENT;

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+1, @max_game_node_id+2, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+1, @max_game_node_id+3, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+2, @max_game_node_id+4, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+2, @max_game_node_id+5, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+3, @max_game_node_id+6, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+3, @max_game_node_id+7, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+4, @max_game_node_id+8,  @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+4, @max_game_node_id+9,  @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+5, @max_game_node_id+10, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+5, @max_game_node_id+11, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+6, @max_game_node_id+12, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+6, @max_game_node_id+13, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+7, @max_game_node_id+14, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+7, @max_game_node_id+15, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+8,  @max_game_node_id+16, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+8,  @max_game_node_id+17, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+9,  @max_game_node_id+18, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+9,  @max_game_node_id+19, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+10, @max_game_node_id+20, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+10, @max_game_node_id+21, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+11, @max_game_node_id+22, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+11, @max_game_node_id+23, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+12, @max_game_node_id+24, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+12, @max_game_node_id+25, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+13, @max_game_node_id+26, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+13, @max_game_node_id+27, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+14, @max_game_node_id+28, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+14, @max_game_node_id+29, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+15, @max_game_node_id+30, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+15, @max_game_node_id+31, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+16, @max_game_node_id+32, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+16, @max_game_node_id+33, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+17, @max_game_node_id+34, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+17, @max_game_node_id+35, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+18, @max_game_node_id+36, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+18, @max_game_node_id+37, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+19, @max_game_node_id+38, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+19, @max_game_node_id+39, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+20, @max_game_node_id+40, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+20, @max_game_node_id+41, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+21, @max_game_node_id+42, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+21, @max_game_node_id+43, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+22, @max_game_node_id+44, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+22, @max_game_node_id+45, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+23, @max_game_node_id+46, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+23, @max_game_node_id+47, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+24, @max_game_node_id+48, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+24, @max_game_node_id+49, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+25, @max_game_node_id+50, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+25, @max_game_node_id+51, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+26, @max_game_node_id+52, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+26, @max_game_node_id+53, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+27, @max_game_node_id+54, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+27, @max_game_node_id+55, @visitor_id);

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+28, @max_game_node_id+56, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+28, @max_game_node_id+57, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+29, @max_game_node_id+58, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+29, @max_game_node_id+59, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+30, @max_game_node_id+60, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+30, @max_game_node_id+61, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+31, @max_game_node_id+62, @home_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+31, @max_game_node_id+63, @visitor_id);

INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 1);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 2);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 3);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 4);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 5);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 6);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 7);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 8);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 9);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 10);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 11);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 12);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 13);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 14);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 15);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 16);

INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 1);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 2);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 3);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 4);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 5);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 6);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 7);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 8);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 9);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 10);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 11);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 12);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 13);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 14);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 15);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 16);

INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 1);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 2);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 3);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 4);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 5);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 6);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 7);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 8);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 9);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 10);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 11);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 12);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 13);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 14);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 15);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 16);

INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 1);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 2);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 3);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 4);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 5);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 6);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 7);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 8);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 9);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 10);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 11);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 12);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 13);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 14);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 15);
INSERT INTO SEED (TOURNAMENT_TYPE_ID, SEED_NO) VALUES (@max_tournament_type_id+1, 16);

INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+32, @max_seed_id+1,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+32, @max_seed_id+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+33, @max_seed_id+8,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+33, @max_seed_id+9,  @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+34, @max_seed_id+5,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+34, @max_seed_id+12, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+35, @max_seed_id+4,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+35, @max_seed_id+13, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+36, @max_seed_id+6,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+36, @max_seed_id+11, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+37, @max_seed_id+3,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+37, @max_seed_id+14, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+38, @max_seed_id+7,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+38, @max_seed_id+10, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+39, @max_seed_id+2,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+39, @max_seed_id+15, @visitor_id);

INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+40, @max_seed_id+1 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+40, @max_seed_id+16+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+41, @max_seed_id+8 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+41, @max_seed_id+9 +16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+42, @max_seed_id+5 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+42, @max_seed_id+12+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+43, @max_seed_id+4 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+43, @max_seed_id+13+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+44, @max_seed_id+6 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+44, @max_seed_id+11+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+45, @max_seed_id+3 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+45, @max_seed_id+14+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+46, @max_seed_id+7 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+46, @max_seed_id+10+16, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+47, @max_seed_id+2 +16, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+47, @max_seed_id+15+16, @visitor_id);

INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+48, @max_seed_id+1 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+48, @max_seed_id+16+16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+49, @max_seed_id+8 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+49, @max_seed_id+9 +16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+50, @max_seed_id+5 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+50, @max_seed_id+12+16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+51, @max_seed_id+4 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+51, @max_seed_id+13+16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+52, @max_seed_id+6 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+52, @max_seed_id+11+16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+53, @max_seed_id+3 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+53, @max_seed_id+14+16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+54, @max_seed_id+7 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+54, @max_seed_id+10+16*2, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+55, @max_seed_id+2 +16*2, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+55, @max_seed_id+15+16*2, @visitor_id);

INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+56, @max_seed_id+1 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+56, @max_seed_id+16+16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+57, @max_seed_id+8 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+57, @max_seed_id+9 +16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+58, @max_seed_id+5 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+58, @max_seed_id+12+16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+59, @max_seed_id+4 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+59, @max_seed_id+13+16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+60, @max_seed_id+6 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+60, @max_seed_id+11+16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+61, @max_seed_id+3 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+61, @max_seed_id+14+16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+62, @max_seed_id+7 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+62, @max_seed_id+10+16*3, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+63, @max_seed_id+2 +16*3, @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+63, @max_seed_id+15+16*3, @visitor_id);
