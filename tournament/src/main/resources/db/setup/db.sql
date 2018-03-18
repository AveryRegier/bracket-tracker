# Copyright (C) 2003-2013 Avery J. Regier.
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

DROP DATABASE IF EXISTS TOURNAMENT;
CREATE DATABASE TOURNAMENT;
USE TOURNAMENT;

CREATE TABLE LEAGUE (
	LEAGUE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL
);

CREATE TABLE TEAM (
	TEAM_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL
);

CREATE TABLE TEAM_SYNONYM (
	TEAM_ID INTEGER NOT NULL,
	NAME VARCHAR(70) NOT NULL,
	FOREIGN KEY(TEAM_ID) REFERENCES TEAM(TEAM_ID)
);

CREATE TABLE LEAGUE_TEAM (
	LEAGUE_ID INTEGER NOT NULL,
	TEAM_ID INTEGER NOT NULL,
	UNIQUE(LEAGUE_ID, TEAM_ID),
	FOREIGN KEY(LEAGUE_ID) REFERENCES LEAGUE(LEAGUE_ID),
	FOREIGN KEY(TEAM_ID) REFERENCES TEAM(TEAM_ID)
);

CREATE TABLE LEVEL (
	LEVEL_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL,
	ROUND_NO INTEGER
);

CREATE TABLE SCORE_SYSTEM (
	SCORE_SYSTEM_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL
);

CREATE TABLE SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID INTEGER NOT NULL,
	LEVEL_ID INTEGER NOT NULL,
	POINTS INTEGER NOT NULL,
	MULTIPLIER INTEGER NOT NULL DEFAULT 0,
	UNIQUE(SCORE_SYSTEM_ID, LEVEL_ID),
	FOREIGN KEY(SCORE_SYSTEM_ID) REFERENCES SCORE_SYSTEM(SCORE_SYSTEM_ID),
	FOREIGN KEY(LEVEL_ID) REFERENCES LEVEL(LEVEL_ID)
);

CREATE TABLE GAME_FEEDER_TYPE (
	GAME_FEEDER_TYPE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70)
);

INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Normal');
INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Lowest Seed');
INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Highest Seed');

CREATE TABLE GAME_NODE (
	GAME_NODE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	LEVEL_ID INTEGER NOT NULL,
	GAME_FEEDER_TYPE_ID INTEGER NOT NULL DEFAULT 1,
	FOREIGN KEY(LEVEL_ID) REFERENCES LEVEL(LEVEL_ID), 
	FOREIGN KEY(GAME_FEEDER_TYPE_ID) REFERENCES GAME_FEEDER_TYPE(GAME_FEEDER_TYPE_ID)
);

CREATE TABLE TOURNAMENT_TYPE (
	TOURNAMENT_TYPE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL,
	LAST_GAME_NODE_ID INTEGER NOT NULL,
	FOREIGN KEY(LAST_GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID)
);

CREATE TABLE OPPONENT (
	OPPONENT_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	TOURNAMENT_TYPE_ID INTEGER NOT NULL,
	SEQUENCE INTEGER NOT NULL,
	NAME VARCHAR(70),
	FOREIGN KEY(TOURNAMENT_TYPE_ID) REFERENCES TOURNAMENT_TYPE(TOURNAMENT_TYPE_ID)
);

CREATE TABLE SEED (
	SEED_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	TOURNAMENT_TYPE_ID INTEGER NOT NULL,
	SEED_NO INTEGER NOT NULL,
	FOREIGN KEY(TOURNAMENT_TYPE_ID) REFERENCES TOURNAMENT_TYPE(TOURNAMENT_TYPE_ID)
);

CREATE TABLE GAME_SEED (
	GAME_NODE_ID INTEGER NOT NULL,
	SEED_ID INTEGER NOT NULL,
	OPPONENT_ID INTEGER NOT NULL,
	UNIQUE (GAME_NODE_ID, OPPONENT_ID),
	FOREIGN KEY(GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID),
	FOREIGN KEY(SEED_ID) REFERENCES SEED(SEED_ID),
	FOREIGN KEY(OPPONENT_ID) REFERENCES OPPONENT(OPPONENT_ID)
);

CREATE TABLE GAME_FEEDER (
	GAME_NODE_ID INTEGER NOT NULL,
	FEEDER_GAME_NODE_ID INTEGER NOT NULL,
	OPPONENT_ID INTEGER NOT NULL,
	UNIQUE(GAME_NODE_ID, FEEDER_GAME_NODE_ID, OPPONENT_ID),
	FOREIGN KEY(GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID),
	FOREIGN KEY(FEEDER_GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID),
	FOREIGN KEY(OPPONENT_ID) REFERENCES OPPONENT(OPPONENT_ID)
);

CREATE TABLE TOURNAMENT (
	TOURNAMENT_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL,
	TOURNAMENT_TYPE_ID INTEGER,
	LEAGUE_ID INTEGER,
	START_TIME TIMESTAMP NOT NULL,
	PARENT_TOURNAMENT_ID INTEGER,
	START_LEVEL_ID INTEGER,
	FOREIGN KEY(TOURNAMENT_TYPE_ID) REFERENCES TOURNAMENT_TYPE(TOURNAMENT_TYPE_ID),
	FOREIGN KEY(LEAGUE_ID) REFERENCES LEAGUE(LEAGUE_ID),
	FOREIGN KEY(PARENT_TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID),
	FOREIGN KEY(START_LEVEL_ID) REFERENCES LEVEL(LEVEL_ID)
);

CREATE TABLE TOURNAMENT_SEED (
	TOURNAMENT_ID INTEGER NOT NULL,
	TEAM_ID INTEGER NOT NULL,
	SEED_ID INTEGER NOT NULL,
	UNIQUE(TOURNAMENT_ID, TEAM_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID),
	FOREIGN KEY(TEAM_ID) REFERENCES TEAM(TEAM_ID),
	FOREIGN KEY(SEED_ID) REFERENCES SEED(SEED_ID)
);

CREATE TABLE GAME (
	GAME_NODE_ID INTEGER NOT NULL,
	TOURNAMENT_ID INTEGER NOT NULL,
	WINNER INTEGER,
	START_TIME TIMESTAMP NULL,
	UNIQUE(GAME_NODE_ID, TOURNAMENT_ID),
	FOREIGN KEY(GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID),
	FOREIGN KEY(WINNER) REFERENCES OPPONENT(OPPONENT_ID)
);

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


CREATE TABLE PLAYER (
	PLAYER_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70),
	LOGIN_ID CHAR(7),
	PASSWORD CHAR(10),
	SITE_ADMIN CHAR(1),
	EMAIL CHAR(254)
);

CREATE TABLE TOURNAMENT_ADMIN (
	TOURNAMENT_ID INTEGER NOT NULL,
	ADMIN_ID INTEGER NOT NULL,
	UNIQUE(TOURNAMENT_ID, ADMIN_ID),
	FOREIGN KEY(ADMIN_ID) REFERENCES PLAYER(PLAYER_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID)
);

CREATE TABLE GROUP_NAME (
	GROUP_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70),
	ADMIN_ID INTEGER NOT NULL,
	INVITATION_CODE INTEGER,
	PARENT_GROUP_ID INTEGER,
	FOREIGN KEY(ADMIN_ID) REFERENCES PLAYER(PLAYER_ID),
	FOREIGN KEY(PARENT_GROUP_ID) REFERENCES GROUP_NAME(GROUP_ID)
);

CREATE TABLE PLAYER_GROUP (
	GROUP_ID INTEGER NOT NULL,
	PLAYER_ID INTEGER NOT NULL,
	UNIQUE(GROUP_ID, PLAYER_ID),
	FOREIGN KEY(GROUP_ID) REFERENCES GROUP_NAME(GROUP_ID),
	FOREIGN KEY(PLAYER_ID) REFERENCES PLAYER(PLAYER_ID)
);

CREATE TABLE TIE_BREAKER_TYPE (
	TIE_BREAKER_TYPE_ID INTEGER NOT NULL PRIMARY KEY,
	NAME VARCHAR(70)
);

INSERT INTO TIE_BREAKER_TYPE (TIE_BREAKER_TYPE_ID, NAME) VALUES (0, 'None');
INSERT INTO TIE_BREAKER_TYPE (TIE_BREAKER_TYPE_ID, NAME) VALUES (1, 'Closest Number');
INSERT INTO TIE_BREAKER_TYPE (TIE_BREAKER_TYPE_ID, NAME) VALUES (2, 'Upset Prediction Delta');
INSERT INTO TIE_BREAKER_TYPE (TIE_BREAKER_TYPE_ID, NAME) VALUES (3, 'Upset Prediction Delta followed by Closest Number');

CREATE TABLE POOL (
	POOL_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(70) NOT NULL,
	GROUP_ID INTEGER NOT NULL,
	SCORE_SYSTEM_ID INTEGER NOT NULL,
	TOURNAMENT_ID INTEGER NOT NULL,
	BRACKET_LIMIT INTEGER NOT NULL DEFAULT 0,
	SHOW_EARLY CHAR(1) NOT NULL DEFAULT 'Y',
	TIE_BREAKER_TYPE_ID INTEGER NOT NULL DEFAULT 0,
	TIE_BREAKER_QUESTION VARCHAR(254),
	TIE_BREAKER_ANSWER CHAR(30),
	FOREIGN KEY(GROUP_ID) REFERENCES GROUP_NAME(GROUP_ID),
	FOREIGN KEY(SCORE_SYSTEM_ID) REFERENCES SCORE_SYSTEM(SCORE_SYSTEM_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID),
	FOREIGN KEY(TIE_BREAKER_TYPE_ID) REFERENCES TIE_BREAKER_TYPE(TIE_BREAKER_TYPE_ID)
);

CREATE TABLE BRACKET (
	BRACKET_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	PLAYER_ID INTEGER NOT NULL,
	TOURNAMENT_ID INTEGER NOT NULL,
	NAME VARCHAR(70) NOT NULL,
	FOREIGN KEY(PLAYER_ID) REFERENCES PLAYER(PLAYER_ID),
	FOREIGN KEY(TOURNAMENT_ID) REFERENCES TOURNAMENT(TOURNAMENT_ID)
);

CREATE TABLE BRACKET_POOL (
	BRACKET_ID INTEGER NOT NULL,
	POOL_ID INTEGER NULL DEFAULT NULL,
	TIE_BREAKER_ANSWER CHAR(30),
	UNIQUE(BRACKET_ID, POOL_ID),
	FOREIGN KEY(BRACKET_ID) REFERENCES BRACKET(BRACKET_ID),
	FOREIGN KEY(POOL_ID) REFERENCES POOL(POOL_ID)
);

CREATE TABLE PICK (
	BRACKET_ID INTEGER NOT NULL,
	GAME_NODE_ID INTEGER NOT NULL,
	WINNER INTEGER,
	FOREIGN KEY(BRACKET_ID) REFERENCES BRACKET(BRACKET_ID),
	FOREIGN KEY(GAME_NODE_ID) REFERENCES GAME_NODE(GAME_NODE_ID),
	FOREIGN KEY(WINNER) REFERENCES OPPONENT(OPPONENT_ID)
);

INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('One', 1);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Two', 2);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Sweet Sixteen', 3);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Elite Eight', 4);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Final Four', 5);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Championship', 6);

INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Wild Card', 1);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Divisional Playoffs', 2);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Conference Championship', 3);
INSERT INTO LEVEL (NAME, ROUND_NO) VALUES ('Super Bowl', 4);

INSERT INTO SCORE_SYSTEM (NAME) VALUES('Simple');

INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 1, 1);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 2, 2);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 3, 3);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 4, 4);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 5, 5);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 6, 6);

INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 7, 1);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 8, 2);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 9, 3);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (1, 10, 4);

INSERT INTO SCORE_SYSTEM (NAME) VALUES('Fibonachi');

INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 1, 1);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 2, 2);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 3, 3);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 4, 5);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 5, 8);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 6, 13);

INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 7, 1);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 8, 2);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 9, 3);
INSERT INTO SCORE_SYSTEM_DETAIL (
	SCORE_SYSTEM_ID, LEVEL_ID, POINTS
) VALUES (2, 10, 5);

#INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Winner Advances');
#INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Loser Advances');
#INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Highest Seed Advances');
#INSERT INTO GAME_FEEDER_TYPE (NAME) VALUES ('Lowest Seed Advances');
