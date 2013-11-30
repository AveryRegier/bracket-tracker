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

# Use this tournament type for ACC and Big 10 tournaments

USE TOURNAMENT;

SELECT @max_tournament_type_id:=IFNULL(MAX(TOURNAMENT_TYPE_ID), 0) FROM TOURNAMENT_TYPE;
SELECT @max_game_node_id:=IFNULL(MAX(GAME_NODE_ID), 0) FROM GAME_NODE;
SELECT @max_seed_id:=IFNULL(MAX(SEED_ID), 0) FROM SEED;
SELECT @max_opponent_id:=IFNULL(MAX(OPPONENT_ID), 0) FROM OPPONENT;

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (4);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (3);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (2);

INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);
INSERT INTO GAME_NODE (LEVEL_ID) VALUES (1);

INSERT INTO TOURNAMENT_TYPE (
	NAME, LAST_GAME_NODE_ID
) VALUES ('11 Team', @max_game_node_id+1);

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

INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+4, @max_game_node_id+8, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+6, @max_game_node_id+9, @visitor_id);
INSERT INTO GAME_FEEDER (GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID) VALUES(@max_game_node_id+7, @max_game_node_id+10, @visitor_id);

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

INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+4,  @max_seed_id+1,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+8,  @max_seed_id+8,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+8,  @max_seed_id+9,  @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+5,  @max_seed_id+4,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+5,  @max_seed_id+5,  @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+9,  @max_seed_id+7,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+9,  @max_seed_id+10, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+6,  @max_seed_id+2,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+10, @max_seed_id+6,  @home_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+10, @max_seed_id+11, @visitor_id);
INSERT INTO GAME_SEED (GAME_NODE_ID, SEED_ID, OPPONENT_ID) VALUES (@max_game_node_id+7,  @max_seed_id+3,  @home_id);
