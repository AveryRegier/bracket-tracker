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

/*
 * Created on Mar 14, 2004
 */
package com.tournamentpool.controller;

import java.sql.SQLException;
import java.util.List;

import utility.domain.Reference;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.beans.BracketBean;
import com.tournamentpool.controller.BracketVisitor.Node;
import com.tournamentpool.domain.Bracket;
import com.tournamentpool.domain.Bracket.Pick;
import com.tournamentpool.domain.Game;
import com.tournamentpool.domain.GameNode;
import com.tournamentpool.domain.GameNode.Feeder;
import com.tournamentpool.domain.Pool;
import com.tournamentpool.domain.ScoreSystem;
import com.tournamentpool.domain.Seed;
import com.tournamentpool.domain.Tournament;
import com.tournamentpool.domain.TournamentType;

/**
 * @author Avery J. Regier
 */
public class ShowTournamentController extends TournamentController {

	/**
	 * @param sp
	 */
	public ShowTournamentController(SingletonProvider sp) {
		super(sp);
	}

	/**
	 * @param string
	 */
	public BracketBean<TournamentVisitor.Node> getTournamentBracket(int tournamentKey) {
		BracketBean<TournamentVisitor.Node> bean = new BracketBean<TournamentVisitor.Node>();
		Tournament tournament = getTournament(tournamentKey);
		bean.setName(tournament.getName());
		bean.setOid(tournament.getOid());
		TournamentType tournamentType = tournament.getTournamentType();
		bean.setOponents(tournamentType.getOpponents());

		TournamentVisitor visitor = new TournamentVisitor(tournament);
		List<TournamentVisitor.Node> nodes = visitor.visit();
		visitForUpset(tournament, nodes);

		bean.setBracket(nodes);
		bean.setMaxLevels(tournamentType.getChampionshipGameNode().getLevel().getRoundNo());

		return bean;
	}

	/**
	 * @param tournamentKey
	 * @return
	 */
	private Tournament getTournament(int tournamentKey) {
		Tournament tournament =
			sp.getSingleton().getTournamentManager().getTournament(tournamentKey);
		return tournament;
	}

	public BracketBean<TournamentVisitor.Node> getTournamentBracketToEdit(int tournamentKey) {
		BracketBean<TournamentVisitor.Node> bean = getTournamentBracket(tournamentKey);
		List<TournamentVisitor.Node> nodes = bean.getBracket();
		Tournament tournament = getTournament(tournamentKey);

		TournamentType tournamentType = tournament.getTournamentType();
		for (TournamentVisitor.Node node: nodes) {
			node.setPick(tournament.getGame(tournamentType.getGameNode(node.getGameNodeOid())));
		}

		return bean;
	}

	/**
	 * @param parameter
	 * @return
	 * @throws SQLException
	 * @throws NumberFormatException
	 */
	public BracketBean<? extends GameVisitorCommon.Node> getBracket(String bracketOID, String poolOID, boolean view) throws NumberFormatException, SQLException {
		Bracket bracket =
			sp.getSingleton().getBracketManager().getBracket(
				Integer.parseInt(bracketOID));
		Tournament tournament = bracket.getTournament();

		BracketBean<? extends GameVisitorCommon.Node> bean;
		if(view) {
			BracketVisitor visitor = new BracketVisitor(sp, tournament, bracket);
			List<Node> nodes = visitor.visit();
			ScoreSystem scoreSystem = null;
			if(poolOID != null) {
				int oid = Integer.parseInt(poolOID);
				Pool pool = sp.getSingleton().getUserManager().getPoolObject(oid);
				scoreSystem = pool.getScoreSystem();
			}
			if(scoreSystem == null) scoreSystem = sp.getSingleton().getScoreSystemManager().getScoreSystem(1); // default it
			if(scoreSystem != null) {
				visitForScore(bracket, nodes, scoreSystem);
			}
			visitForUpset(bracket.getTournament(), nodes);
			BracketBean<BracketVisitor.Node> bean1 = new BracketBean<BracketVisitor.Node>();
			bean1.setBracket(nodes);
			bean = bean1;
		} else {
			TournamentVisitor visitor = new TournamentVisitor(tournament);
			List<TournamentVisitor.Node> nodes = visitor.visit();

			// set the winners
			TournamentType tournamentType = tournament.getTournamentType();
			for (TournamentVisitor.Node node : nodes) {
				node.setPick(bracket.getPick(sp, tournamentType.getGameNode(node.getGameNodeOid())));
			}
			BracketBean<TournamentVisitor.Node> bean1 = new BracketBean<TournamentVisitor.Node>();
			bean1.setBracket(nodes);
			bean = bean1;
		}
		bean.setOid(bracket.getOID());
		bean.setName(bracket.getName());
		bean.setOponents(tournament.getTournamentType().getOpponents());
		bean.setMaxLevels(tournament.getTournamentType().getChampionshipGameNode().getLevel().getRoundNo());

		return bean;
	}

	/**
	 * @param bracket
	 * @param tournament
	 * @param nodes
	 * @param scoreSystem
	 */
	private void visitForScore(Bracket bracket, List<Node> nodes, ScoreSystem scoreSystem) {
		Tournament tournament = bracket.getTournament();
		TournamentType tournamentType = tournament.getTournamentType();
		TournamentVisitor tournamentVisitor = new TournamentVisitor(tournament);
		ScoreSystem.Score theScore = scoreSystem.new Score();
		for (Node node : nodes) {
			GameNode gameNode = tournamentType.getGameNode(node.getGameNodeOid());
			if(gameNode != null) {
				Seed winner = gameNode.visitForWinner(tournamentVisitor);
				int score = scoreSystem.getScore(gameNode.getLevel());
				Game game = tournament.getGame(gameNode);
				boolean played = game != null && game.getWinner() != null;
				if(played) {
					node.setDecided(winner == node.getSeed(), score);
					theScore.add(score);
				} else {
					Seed preWinner;
					do {
						Feeder feeder = gameNode.getFeeder(bracket.getPickFromMemory(gameNode).getWinner());
						preWinner = feeder.visitForWinner(tournamentVisitor);
						Reference ref = feeder.getFeeder();
						if(ref instanceof GameNode) {
							gameNode = (GameNode) ref;
						}
					} while( preWinner == null);
					if(preWinner != node.getSeed()) node.setDecided(false, 0);
					else {
						node.setPossible(score);
						theScore.predict(score, bracket.getPickFromMemory(gameNode));
					}
				}
			}
		}
	}

	/**
	 * @param bracket
	 * @param tournament
	 * @param nodes
	 * @param scoreSystem
	 */
	private void visitForUpset(Tournament tournament, List<? extends GameVisitorCommon.Node> nodes) {
		TournamentVisitor tournamentVisitor = new TournamentVisitor(tournament);
		TournamentType tournamentType = tournament.getTournamentType();
		for (GameVisitorCommon.Node node: nodes) {
			GameNode gameNode = tournamentType.getGameNode(node.getGameNodeOid());
			if(gameNode != null) {
				Seed winner = gameNode.visitForWinner(tournamentVisitor);
				Game game = tournament.getGame(gameNode);
				boolean played = game != null && game.getWinner() != null;
				if(played) {
					Seed loser = gameNode.visitForLoser(tournamentVisitor);
					if(loser != null) {
						node.setUpset(winner.getSeedNo() > loser.getSeedNo());
//						node.setUpset(winner.getSeedNo() > loser.getSeedNo(), winner, loser);
					}
				}
			}
		}
	}

	private void visitForUpsetPicked(Bracket bracket, List<BracketVisitor.Node> nodes) throws SQLException {
		BracketVisitor bracketVisitor = new BracketVisitor(sp, bracket.getTournament(), bracket);
		TournamentType tournamentType = bracket.getTournament().getTournamentType();
		for (BracketVisitor.Node node: nodes) {
			GameNode gameNode = tournamentType.getGameNode(node.getGameNodeOid());
			if(gameNode != null) {
				Seed winner = gameNode.visitForWinner(bracketVisitor);
				Pick pick = bracket.getPick(sp, gameNode);
				boolean picked = pick != null && pick.getWinner() != null;
				if(picked) {
					Seed loser = gameNode.visitForLoser(bracketVisitor);
					if(loser != null) {
						node.setUpsetPicked(winner.getSeedNo() > loser.getSeedNo());
//						node.setUpsetPicked(winner.getSeedNo() > loser.getSeedNo(), winner, loser);
					}
				}
			}
		}
	}

	public int getUpsetPredictionDelta(Bracket bracket, ScoreSystem scoreSystem) throws SQLException {
		Tournament tournament = bracket.getTournament();
		BracketVisitor visitor = new BracketVisitor(sp, tournament, bracket);
		List<BracketVisitor.Node> nodes = visitor.visit();
		visitForScore(bracket, nodes, scoreSystem);
		visitForUpsetPicked(bracket, nodes);
		visitForUpset(bracket.getTournament(), nodes);
		int delta = 0;
		for (BracketVisitor.Node node: nodes) {
			if(node.isDecided()) { // I don't get docked or credited unless the game is done
				if(node.isUpset()) {
					if(node.isCorrect()) {
						delta++; // get rewarded if I did pick the upset
					} else {
						delta--; // get docked if I did not pick the upset
					}
				// it is possible to have nodes without upset possibilities
				} else if(node.isUpsetPicked() && !node.isCorrect()) {
					delta--; // get docked if I picked an upset that didn't happen
				}
			}
		}
		return delta;
	}

/*
	public UpsetPredictionDelta getUpsetPredictionDelta(Bracket bracket, ScoreSystem scoreSystem) throws SQLException {
		Tournament tournament = bracket.getTournament();
		BracketVisitor visitor = new BracketVisitor(sp, tournament, bracket);
		List nodes = visitor.visit();
		visitForScore(bracket, nodes, scoreSystem);
		visitForUpsetPicked(bracket, nodes);
		visitForUpset(bracket.getTournament(), nodes);
		UpsetPredictionDelta delta = new UpsetPredictionDelta();
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			BracketVisitor.Node node = (BracketVisitor.Node) iter.next();
			// I don't get docked or credited unless the game is done
			if(!node.isSeedNode() && node.isDecided() && node.isPickPossible()) {
				if(node.isUpset()) {
					delta.addUpset(node.isCorrect());
				// it is possible to have nodes without upset possibilities
				} else if(node.isUpsetPicked() && !node.isCorrect()) {
					delta.addIncorrectPick();
				}
			}
		}
		return delta;
	}
	public UpsetPredictionRiskDelta getUpsetPredictionRiskDelta(Bracket bracket, ScoreSystem scoreSystem) throws SQLException {
		Tournament tournament = bracket.getTournament();
		BracketVisitor visitor = new BracketVisitor(sp, tournament, bracket);
		List nodes = visitor.visit();
		visitForScore(bracket, nodes, scoreSystem);
		visitForUpsetPicked(bracket, nodes);
		visitForUpset(bracket.getTournament(), nodes);
		UpsetPredictionRiskDelta delta = new UpsetPredictionRiskDelta();
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			BracketVisitor.Node node = (BracketVisitor.Node) iter.next();
			if(!node.isSeedNode() && node.isDecided()) { // I don't get docked or credited unless the game is done
				if(node.isUpset()) {
					delta.addUpset(node.isCorrect());
				// it is possible to have nodes without upset possibilities
				} else if(node.isUpsetPicked() && !node.isCorrect()) {
					delta.addIncorrectPick();
				}
			}
		}
		return delta;
	}
*/
}
