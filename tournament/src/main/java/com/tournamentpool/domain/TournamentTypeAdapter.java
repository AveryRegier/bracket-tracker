/* 
Copyright (C) 2003-2008 Avery J. Regier.

This file is part of the Tournament Pool and Bracket Tracker.

Tournament Pool and Bracket Tracker is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Tournament Pool and Bracket Tracker is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.tournamentpool.domain;

import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class TournamentTypeAdapter implements TournamentType {

	public Collection<GameNode> getGameNodes() {
        return streamGameNodes()
                .collect(Collectors.toList());
    }

	public List<GameNode> getGameNodesInLevelOrder() {
        return streamGameNodesInLevelOrder()
                .collect(Collectors.toList());
	}

    public Stream<GameNode> streamGameNodesInLevelOrder() {
        return streamGameNodes()
                .collect(Collectors.groupingBy(GameNode::getLevel))
                .values().stream()
                .flatMap((l) -> l.stream());
    }

    public Menu getLevelMenu() {
		return new ReferenceMenu<Level>("levels") {
			protected Map<Integer, Level> getReferences() {
                return streamGameNodes()
                        .map(GameNode::getLevel)
                        .collect(toTreeMap(Level::getID));
			}
		};
	}

    private static <K,U> Collector<U, ?, TreeMap<K, U>> toTreeMap(Function<U, K> keyMapper) {
        return Collectors.toMap(
            keyMapper,
            Function.identity(),
            (a,b) -> {
                if(a != b) {
                    throw new IllegalStateException(
                            String.format(
                                    "Attempting to accumulate objects that aren't the same: %s != %s", a, b));
                }
                return a;
            },
            TreeMap::new
        );
    }

    public Stream<GameNode> streamGameNodes() {
        return StreamSupport.stream(new GameNodeSpliterator(this), false);
    }


}
