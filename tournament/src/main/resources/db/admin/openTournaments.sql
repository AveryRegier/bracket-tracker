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
# but WITHOUT ANY WARRANTY; 
# without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Administrative help query
# Find all tournaments not yet completed.

SELECT T.TOURNAMENT_ID, T.NAME
  FROM `tournament`.TOURNAMENT AS T,
       (SELECT T.TOURNAMENT_ID 
        FROM `tournament`.TOURNAMENT AS T, `tournament`.TOURNAMENT_TYPE AS TT,
             `tournament`.GAME_NODE AS GN, `tournament`.GAME AS G
        WHERE T.TOURNAMENT_TYPE_ID = TT.TOURNAMENT_TYPE_ID
          AND TT.LAST_GAME_NODE_ID = GN.GAME_NODE_ID
          AND G.TOURNAMENT_ID = T.TOURNAMENT_ID
          AND G.GAME_NODE_ID = GN.GAME_NODE_ID
          AND G.WINNER IS NOT NULL
        ) COMPLETED 
 WHERE T.TOURNAMENT_TYPE_ID <> COMPLETED.TOURNAMENT_ID;
  