# Copyright (C) 2003-2008 Avery J. Regier.
#
# This file is part of the Tournament Pool and Bracket Tracker.
#Ï
# Tournament Pool and Bracket Tracker is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# Tournament Pool and Bracket Tracker is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; 
# without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Administrative help query
# Find all brackets not yet added to available pools in the groups the bracket owner is in.
# Prevents admin brackets from getting added in.

#USE TOURNAMENT;
#INSERT ignore INTO BRACKET_POOL (
SELECT BRACKET_ID, POOL_ID, '0' 
FROM (
SELECT POSSIBLE.*, BP.BRACKET_ID AS PRESENT_BRACKET_ID, BP.POOL_ID AS PRESENT_POOL_ID
FROM
(SELECT U.PLAYER_ID, 
        U.NAME AS PLAYER_NAME, 
		G.GROUP_ID, G.NAME AS GROUP_NAME, 
		G.PROGENITOR_GROUP_ID, 
		P.POOL_ID, P.NAME AS POOL_NAME, 
		B.BRACKET_ID, 
		B.TOURNAMENT_ID, 
		B.NAME, 
		BRACKET_COUNT.NUM_PICKS
FROM 
    (SELECT G.*, W.PARENT_GROUP_ID AS PROGENITOR_GROUP_ID
    FROM TOURNAMENT.GROUP_NAME G JOIN (
        SELECT COALESCE(PARENT.PARENT_GROUP_ID, CHILD.PARENT_GROUP_ID) AS PARENT_GROUP_ID, CHILD.GROUP_ID
        FROM (
            SELECT COALESCE(PARENT.PARENT_GROUP_ID, CHILD.PARENT_GROUP_ID) AS PARENT_GROUP_ID, CHILD.GROUP_ID
            FROM (
				SELECT COALESCE(PARENT.PARENT_GROUP_ID, CHILD.PARENT_GROUP_ID) AS PARENT_GROUP_ID, CHILD.GROUP_ID
				FROM (
					SELECT COALESCE(CHILD.PARENT_GROUP_ID, CHILD.GROUP_ID) AS PARENT_GROUP_ID, CHILD.GROUP_ID
					FROM TOURNAMENT.GROUP_NAME CHILD
				) CHILD 
				JOIN TOURNAMENT.GROUP_NAME PARENT ON PARENT.GROUP_ID = CHILD.PARENT_GROUP_ID
            ) CHILD 
            JOIN TOURNAMENT.GROUP_NAME PARENT ON PARENT.GROUP_ID = CHILD.PARENT_GROUP_ID
        ) CHILD 
        JOIN GROUP_NAME PARENT ON PARENT.GROUP_ID = CHILD.PARENT_GROUP_ID
    ) W ON G.GROUP_ID = W.GROUP_ID) G, 
     TOURNAMENT.PLAYER_GROUP UG, 
     TOURNAMENT.PLAYER U,
     TOURNAMENT.POOL P,
     TOURNAMENT.BRACKET B
    LEFT JOIN (
    SELECT BRACKET_ID, COUNT(*) AS NUM_PICKS 
    FROM TOURNAMENT.PICK
    GROUP BY BRACKET_ID) BRACKET_COUNT ON B.BRACKET_ID = BRACKET_COUNT.BRACKET_ID
WHERE G.GROUP_ID = UG.GROUP_ID 
  AND UG.PLAYER_ID = U.PLAYER_ID
  AND P.GROUP_ID = G.PROGENITOR_GROUP_ID
  AND B.PLAYER_ID = U.PLAYER_ID
  AND B.TOURNAMENT_ID = P.TOURNAMENT_ID
  AND U.PLAYER_ID <> G.ADMIN_ID
) POSSIBLE LEFT JOIN TOURNAMENT.BRACKET_POOL BP ON BP.POOL_ID = POSSIBLE.POOL_ID AND BP.BRACKET_ID = POSSIBLE.BRACKET_ID 
WHERE BP.BRACKET_ID IS NULL 
  AND POSSIBLE.NUM_PICKS = 63
  and player_id not in (
	select player_id 
	from tournament.bracket_pool bp join bracket ib on bp.bracket_id = ib.bracket_id 
	where ib.tournament_id = possible.tournament_id 
      and pool_id=bp.pool_id)
) FOO 
#)
;

