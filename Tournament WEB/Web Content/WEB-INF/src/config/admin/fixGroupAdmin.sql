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

# Administrative cleanup query
# Add all group administrators as members of the groups they administrate.
# This can fix situations where a group becomes abandoned due to an 
# administrative error.

insert into TOURNAMENT.player_group (
select g2.group_id, g2.admin_id as player_id from TOURNAMENT.group_name g2 left join (
select g.* from TOURNAMENT.group_name g, TOURNAMENT.player_group pg where g.GROUP_ID = pg.GROUP_ID and g.ADMIN_ID = pg.PLAYER_ID) j on g2.group_id = j.group_id 
where j.group_id is null)