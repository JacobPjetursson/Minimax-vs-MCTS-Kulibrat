package game;

import ai.AI;
import ai.MCTS.MCTS;
import ai.Minimax.LookupTableMinimax;
import ai.Minimax.Minimax;
import ai.Minimax.Temp;
import ai.Minimax.Zobrist;
import gui.EndGamePane;
import gui.NavPane;
import gui.PlayArea;
import gui.PlayPane;
import gui.board.BoardPiece;
import gui.board.BoardTile;
import gui.board.Goal;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static misc.Globals.*;

public class Controller {
    private int mode;
    private AI aiRed;
    private AI aiBlack;
    private Button startAIButton;
    private Button stopAIButton;
    private Thread aiThread;
    private NavPane navPane;

    private State state;
    private PlayArea playArea;

    private boolean endGamePopup;

    public Controller(PlayPane playPane, int playerRedInstance, int playerBlackInstance, int pointsToWin, int mode, int redTime, int blackTime, boolean overwriteDB) {
        Zobrist.initialize(); // Generate random numbers for state configs
        this.mode = mode;
        state = new State(pointsToWin);
        endGamePopup = false;

        navPane = playPane.getNavPane();
        playArea = playPane.getPlayArea();

        if (playerRedInstance != HUMAN) {
            if (playerRedInstance == MINIMAX) {
                aiRed = new Minimax(RED, redTime);
            } else if (playerRedInstance == LOOKUP_TABLE) {
                aiRed = new LookupTableMinimax(RED, state, overwriteDB);
            } else {
                aiRed = new MCTS(state, RED, redTime);

            }
        }
        if (playerBlackInstance != HUMAN) {
            if (playerBlackInstance == MINIMAX) {
                aiBlack = new Minimax(BLACK, blackTime);
            } else if (playerBlackInstance == LOOKUP_TABLE) {
                aiBlack = new LookupTableMinimax(BLACK, state, overwriteDB);
            } else {
                aiBlack = new MCTS(state, BLACK, blackTime);
            }
        }

        // Fetch all gui elements that invoke something game-related
        BoardTile[][] tiles = playArea.getBoard().getTiles();
        startAIButton = navPane.getStartAIButton();
        stopAIButton = navPane.getStopAIButton();
        Goal goalRed = playArea.getGoal(RED);
        Goal goalBlack = playArea.getGoal(BLACK);

        if (mode == AI_VS_AI) navPane.addAIWidgets(mode);


        // Set event handlers for said elements
        // Tiles
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                BoardTile tile = tiles[i][j];
                tile.setOnMouseClicked(event -> {
                    if (tile.getHighlight()) {
                        BoardPiece piece = playArea.getSelected();
                        doHumanTurn(new Move(piece.getRow(), piece.getCol(), tile.getRow(), tile.getCol(), piece.getTeam()));
                    }
                });
            }
        }
        startAIButton.setOnMouseClicked(event -> {
            startAI();
            stopAIButton.setDisable(false);
        });
        // Stop AI button
        stopAIButton.setDisable(true);
        stopAIButton.setOnMouseClicked(event -> {
            aiThread.interrupt();
            stopAIButton.setDisable(true);
        });
        // Goal Red
        goalRed.setOnMouseClicked(event -> {
            if (goalRed.getHighlight()) {
                BoardPiece piece = playArea.getSelected();
                doHumanTurn(new Move(piece.getRow(), piece.getCol(), -1, -1, piece.getTeam()));
            }
        });
        // Goal Black
        goalBlack.setOnMouseClicked(event -> {
            if (goalBlack.getHighlight()) {
                BoardPiece piece = playArea.getSelected();
                doHumanTurn(new Move(piece.getRow(), piece.getCol(), -1, -1, piece.getTeam()));
            }
        });
        playArea.update(state);
        if (mode == HUMAN_VS_AI && playerRedInstance != HUMAN) {
            aiThread = new Thread(this::doAITurn);
            //aiThread.setDaemon(true);
            aiThread.start();
        }
    }

    private void doHumanTurn(Move move) {
        state = state.getNextState(move);
        if (aiRed != null) aiRed.update(state);
        if (aiBlack != null) aiBlack.update(state);

        playArea.highlightMoves(move.oldRow, move.oldCol, move.team, false);
        playArea.deselect();
        playArea.update(state);
        checkGameOver();

        if (Logic.gameOver(state)) return;

        // TODO - Info that opponent turn has been passed
        if (state.getTurn() == move.team) {
            System.out.println("TEAM " + ((move.team == RED) ? "Black" : "Red") + "'s turn has been skipped!");
        } else if (mode == HUMAN_VS_AI) {
            aiThread = new Thread(this::doAITurn);
            //aiThread.setDaemon(true);
            aiThread.start();
        }
    }

    private void startAI() {
        // For the AI vs. AI mode. New thread is needed to update the gui while running the AI
        navPane.getRestartButton().setDisable(true);
        navPane.getMenuButton().setDisable(true);
        startAIButton.setDisable(true);

        aiThread = new Thread(() -> {
            try {
                while (!Logic.gameOver(state)) {
                    doAITurn();
                    Thread.sleep(0); // To allow thread interruption

                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                startAIButton.setDisable(false);
                navPane.getMenuButton().setDisable(false);
                navPane.getRestartButton().setDisable(false);
            }

        });
        //aiThread.setDaemon(true);
        aiThread.start();
    }

    private void doAITurn() {
        int turn = state.getTurn();
        Move move;
        if (aiRed != null && turn == RED) {
            move = aiRed.makeMove(state);
        } else {
            move = aiBlack.makeMove(state);
        }
        state = state.getNextState(move);
        if (aiRed != null) aiRed.update(state);
        if (aiBlack != null) aiBlack.update(state);
        // Update gui elements on another thread
        Platform.runLater(() -> {
            playArea.update(state);
            checkGameOver();
        });
        if (Logic.gameOver(state)) return;
        if (turn == state.getTurn()) {
            System.out.println("TEAM " + ((turn == RED) ? "Black" : "Red") + "'s turn has been skipped!");
            if (mode == HUMAN_VS_AI) doAITurn();
        }
    }

    private void checkGameOver() {
        if (Logic.gameOver(state) && !endGamePopup) {
            endGamePopup = true;
            Stage prevStage = (Stage) playArea.getScene().getWindow();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(new EndGamePane(prevStage, Logic.getWinner(state)), 400, 150));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(playArea.getScene().getWindow());
            newStage.setOnCloseRequest(Event::consume);
            newStage.show();
        }
    }
}
