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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import utility.domain.Reference;
import utility.menu.Menu;
import utility.menu.reference.ReferenceMenu;

import com.tournamentpool.domain.GameNode.Feeder;

public abstract class TournamentTypeAdapter implements TournamentType {

	public Map<Object, GameNode> getGameNodes() {
			Map<Object, GameNode> toReturn = new TreeMap<Object, GameNode>();
			List<GameNode> nodes = new ArrayList<GameNode>(getNumSeeds() * 2);
			nodes.add(getChampionshipGameNode());
			while(!nodes.isEmpty()) {
				GameNode node = nodes.remove(0);
				toReturn.put(node.getID(), node);
	//			if(node.getLevel() != startLevel) { // let the feeder wrapping take care of this
					for (Feeder feeder: node.getFeeders()) {
						Reference reference = feeder.getFeeder();
						if(reference instanceof GameNode) {
							nodes.add((GameNode)reference);
						}
					}
	//			}
			}
			return toReturn;
		}

	public List<GameNode> getGameNodesInLevelOrder() {
		Map<Level, List<GameNode>> levelMap = new TreeMap<Level, List<GameNode>>();
		
		Collection<GameNode> values = getGameNodes().values();
		for (GameNode gameNode : values) {
			List<GameNode> nodes = levelMap.get(gameNode.getLevel());
			if(nodes == null) {
				nodes = new ArrayList<GameNode>();
				levelMap.put(gameNode.getLevel(), nodes);
			}
			nodes.add(gameNode);
		}
		
		List<GameNode> nodes = new ArrayList<GameNode>(getNumSeeds() * 2);
		for (Entry<Level, List<GameNode>> entry : levelMap.entrySet()) {
			nodes.addAll(entry.getValue());
		}
		
		return nodes;
	}

	public Menu getLevelMenu() {
		return new ReferenceMenu<Level>("levels") {
			protected Map<Integer, Level> getReferences() {
				Map<Integer, Level> levels = new TreeMap<Integer, Level>();
				List<GameNode> nodes = new ArrayList<GameNode>(getNumSeeds() * 2);
				nodes.add(getChampionshipGameNode());
				while(!nodes.isEmpty()) {
					GameNode node = nodes.remove(0);
					Level level = node.getLevel();
					levels.put(level.getID(), level);
					for (GameNode.Feeder feeder: node.getFeeders()) {
						Reference reference = feeder.getFeeder();
						if(reference instanceof GameNode) {
							nodes.add((GameNode)reference);
						}
					}
				}
				return levels;
			}
		};
	}

}
