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

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.beans.BracketBean;
import com.tournamentpool.controller.BracketVisitor.Node;
import com.tournamentpool.domain.*;
import com.tournamentpool.domain.Bracket.Pick;
import com.tournamentpool.domain.GameNode.Feeder;
import utility.domain.Reference;

import java.util.List;
import java.util.Optional;

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
	 */
	public BracketBean<TournamentVisitor.Node> getTournamentBracket(int tournamentKey) {
		BracketBean<TournamentVisitor.Node> bean = new BracketBean<>();
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
        return sp.getSingleton().getTournamentManager().getTournament(tournamentKey);
	}

	public BracketBean<TournamentVisitor.Node> getTournamentBracketToEdit(int tournamentKey) {
		BracketBean<TournamentVisitor.Node> bean = getTournamentBracket(tournamentKey);
		List<TournamentVisitor.Node> nodes = bean.getBracket();
		Tournament tournament = getTournament(tournamentKey);

		TournamentType tournamentType = tournament.getTournamentType();
		for (TournamentVisitor.Node node: nodes) {
			node.setPick(tournament.getGame(findNode(tournamentType, node)));
		}

		return bean;
	}

    private GameNode findNode(TournamentType tournamentType, TournamentVisitor.Node node) {
        return tournamentType.getGameNode(node.getGameNodeOid()).orElse(null);
    }

    /**
	 * @return
	 * @throws NumberFormatException
	 */
	public BracketBean<? extends GameVisitorCommon.Node> getBracket(String bracketOID, String poolOID, boolean view) throws NumberFormatException {
		Bracket bracket =
			sp.getSingleton().getBracketManager().getBracket(
				Integer.parseInt(bracketOID));
		Tournament tournament = bracket.getTournament();

		BracketBean<? extends GameVisitorCommon.Node> bean = view ?
                visitForView(poolOID, bracket, tournament) :
                visitForEdit(bracket, tournament);
		bean.setOid(bracket.getOID());
		bean.setName(bracket.getName());
		bean.setOponents(tournament.getTournamentType().getOpponents());
		bean.setMaxLevels(tournament.getTournamentType().getChampionshipGameNode().getLevel().getRoundNo());

		return bean;
	}

    private BracketBean<TournamentVisitor.Node> visitForEdit(Bracket bracket, Tournament tournament) {
        TournamentVisitor visitor = new TournamentVisitor(tournament);
        List<TournamentVisitor.Node> nodes = visitor.visit();

        // set the winners
        TournamentType tournamentType = tournament.getTournamentType();
        for (TournamentVisitor.Node node : nodes) {
            node.setPick(bracket.getPick(sp, findNode(tournamentType, node)));
        }
        BracketBean<TournamentVisitor.Node> bean1 = new BracketBean<>();
        bean1.setBracket(nodes);
        return bean1;
    }

    private BracketBean<Node> visitForView(String poolOID, Bracket bracket, Tournament tournament) {
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
        BracketBean<Node> bean1 = new BracketBean<>();
        bean1.setBracket(nodes);
        return bean1;
    }

    /**
	 * @param bracket
	 * @param nodes
	 * @param scoreSystem
	 */
	private void visitForScore(Bracket bracket, List<Node> nodes, ScoreSystem scoreSystem) {
		Tournament tournament = bracket.getTournament();
		TournamentType tournamentType = tournament.getTournamentType();
		TournamentVisitor tournamentVisitor = new TournamentVisitor(tournament);
		ScoreSystem.Score theScore = scoreSystem.new Score();
		for (Node node : nodes) {
			Optional<GameNode> maybeGameNode = tournamentType.getGameNode(node.getGameNodeOid());
			if(maybeGameNode.isPresent()) {
                GameNode gameNode = maybeGameNode.get();
				Seed winner = gameNode.visitForWinner(tournamentVisitor);
				int score = scoreSystem.getScore(gameNode.getLevel());
				Optional<Game> game = tournament.getGame(gameNode);
				boolean played = game.isPresent() && game.get().getWinner().isPresent();
				if(played) {
					node.setDecided(winner == node.getSeed(), score);
					theScore.add(score);
				} else {
					Seed preWinner;
					do {
						Feeder feeder = gameNode.getFeeder(
                                bracket.getPickFromMemory(gameNode)
                                        .orElseThrow(IllegalStateException::new)
                                        .getWinner().orElseThrow(IllegalStateException::new));
						preWinner = feeder.visitForWinner(tournamentVisitor);
						Reference ref = feeder.getFeeder();
						if(ref instanceof GameNode) {
							gameNode = (GameNode) ref;
						}
					} while( preWinner == null);
					if(preWinner != node.getSeed()) node.setDecided(false, 0);
					else {
						node.setPossible(score);
						theScore.predict(score,
                                bracket.getPickFromMemory(gameNode)
                                       .orElseThrow(IllegalStateException::new));
					}
				}
			}
		}
	}

	/**
	 * @param tournament
	 * @param nodes
	 */
	private void visitForUpset(Tournament tournament, List<? extends GameVisitorCommon.Node> nodes) {
		TournamentVisitor tournamentVisitor = new TournamentVisitor(tournament);
		TournamentType tournamentType = tournament.getTournamentType();
		for (GameVisitorCommon.Node node: nodes) {
			Optional<GameNode> maybeGameNode = tournamentType.getGameNode(node.getGameNodeOid());
			if(maybeGameNode.isPresent()) {
                GameNode gameNode = maybeGameNode.get();
				Optional<Game> game = tournament.getGame(gameNode);
                boolean played = game.isPresent() && game.get().getWinner().isPresent();
                if(played) {
                    Seed winner = gameNode.visitForWinner(tournamentVisitor);
                    Seed loser = gameNode.visitForLoser(tournamentVisitor);
					if(loser != null) {
						node.setUpset(winner.getSeedNo() > loser.getSeedNo());
//						node.setUpset(winner.getSeedNo() > loser.getSeedNo(), winner, loser);
					}
				}
			}
		}
	}

	private void visitForUpsetPicked(Bracket bracket, List<BracketVisitor.Node> nodes) {
		BracketVisitor bracketVisitor = new BracketVisitor(sp, bracket.getTournament(), bracket);
		TournamentType tournamentType = bracket.getTournament().getTournamentType();
		for (BracketVisitor.Node node: nodes) {
            Optional<GameNode> maybeGameNode = tournamentType.getGameNode(node.getGameNodeOid());
            if(maybeGameNode.isPresent()) {
                GameNode gameNode = maybeGameNode.get();
				Optional<Pick> pick = bracket.getPick(sp, gameNode);
                boolean picked = pick.isPresent() && pick.get().getWinner().isPresent();
                if(picked) {
                    Seed winner = gameNode.visitForWinner(bracketVisitor);
                    Seed loser = gameNode.visitForLoser(bracketVisitor);
					if(loser != null) {
						node.setUpsetPicked(winner.getSeedNo() > loser.getSeedNo());
//						node.setUpsetPicked(winner.getSeedNo() > loser.getSeedNo(), winner, loser);
					}
				}
			}
		}
	}

	public int getUpsetPredictionDelta(Bracket bracket, ScoreSystem scoreSystem) {
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
	public UpsetPredictionDelta getUpsetPredictionDelta(Bracket bracket, ScoreSystem scoreSystem) {
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
	public UpsetPredictionRiskDelta getUpsetPredictionRiskDelta(Bracket bracket, ScoreSystem scoreSystem) {
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
