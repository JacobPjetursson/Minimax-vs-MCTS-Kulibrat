package game;

import ai.AI;
import ai.MCTS.MCTS;
import ai.Minimax.*;
import gui.*;
import gui.board.BoardPiece;
import gui.board.BoardTile;
import gui.board.Goal;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import misc.Globals;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import static misc.Globals.*;

public class Controller {
    private int mode;
    private int playerRedInstance;
    private int playerBlackInstance;
    private int redTime;
    private int blackTime;
    private boolean overwriteDB;
    private int turnNo;
    private AI aiRed;
    private AI aiBlack;
    private Button startAIButton;
    private Button stopAIButton;
    private Button reviewButton;
    private CheckBox helpHumanBox;
    private Thread aiThread;
    private NavPane navPane;
    public Connection dbConnection;
    private BoardPiece selected;
    private ArrayList<Move> curHighLights;
    private Stage primaryStage;
    private State state;
    private PlayArea playArea;
    private Goal goalRed;
    private Goal goalBlack;
    private boolean endGamePopup;
    private ArrayList<PrevState> previousStates;
    private Window window;


    public Controller(Stage primaryStage, int playerRedInstance, int playerBlackInstance,
                      State state, int redTime, int blackTime, boolean overwriteDB) {
        Zobrist.initialize(); // Generate random numbers for state configs
        this.mode = setMode(playerRedInstance, playerBlackInstance);
        this.playerRedInstance = playerRedInstance;
        this.playerBlackInstance = playerBlackInstance;
        this.redTime = redTime;
        this.blackTime = blackTime;
        this.overwriteDB = overwriteDB;
        this.turnNo = 0;
        this.state = state;
        this.primaryStage = primaryStage;
        this.endGamePopup = false;
        this.curHighLights = new ArrayList<>();
        this.previousStates = new ArrayList<>();

        PlayPane playPane = new PlayPane(this);
        primaryStage.setScene(new Scene(playPane,
                Globals.WIDTH, Globals.HEIGHT));

        navPane = playPane.getNavPane();
        playArea = playPane.getPlayArea();
        window = playArea.getScene().getWindow();

        if (playerRedInstance == MINIMAX) {
            aiRed = new Minimax(RED, redTime);
        } else if (playerRedInstance == LOOKUP_TABLE) {
            aiRed = new LookupTableMinimax(RED, state, overwriteDB);
        } else if (playerRedInstance == MONTE_CARLO) {
            aiRed = new MCTS(state, RED, redTime);
        }

        if (playerBlackInstance == MINIMAX) {
            aiBlack = new Minimax(BLACK, blackTime);
        } else if (playerBlackInstance == LOOKUP_TABLE) {
            if(playerRedInstance == LOOKUP_TABLE) {
                overwriteDB = false;
            }
            aiBlack = new LookupTableMinimax(BLACK, state, overwriteDB);
        } else if (playerBlackInstance == MONTE_CARLO) {
            aiBlack = new MCTS(state, BLACK, blackTime);
        }

        // Fetch all gui elements that invoke something game-related
        startAIButton = navPane.getStartAIButton();
        stopAIButton = navPane.getStopAIButton();
        helpHumanBox = navPane.getHelpHumanBox();
        reviewButton = navPane.getReviewButton();
        goalRed = playArea.getGoal(RED);
        goalBlack = playArea.getGoal(BLACK);


        if (mode == AI_VS_AI) navPane.addAIWidgets();
        if (mode == HUMAN_VS_AI) navPane.addReviewButton();
        if (mode != AI_VS_AI) navPane.addHelpHumanBox();

        // Set event handlers for gui elements
        // Selected piece
        playPane.setFocusTraversable(true);
        playPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) { //Escape to deselect
                deselect();
            }
        });

        // Tiles
        BoardTile[][] tiles = playArea.getBoard().getTiles();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                BoardTile tile = tiles[i][j];
                tile.setOnMouseClicked(event -> {
                    if (tile.getHighlight() || Globals.CUSTOMIZABLE) {
                        BoardPiece piece = selected;
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
        // Review button
        reviewButton.setOnMouseClicked(event -> {
            if(connect(state.getScoreLimit())) {
                reviewGame();
            }
        });
        // Goal Red
        goalRed.setOnMouseClicked(event -> {
            if (goalRed.getHighlight() || Globals.CUSTOMIZABLE) {
                BoardPiece piece = selected;
                doHumanTurn(new Move(piece.getRow(), piece.getCol(), -1, -1, piece.getTeam()));
            }
        });
        // Goal Black
        goalBlack.setOnMouseClicked(event -> {
            if (goalBlack.getHighlight() || Globals.CUSTOMIZABLE) {
                BoardPiece piece = selected;
                doHumanTurn(new Move(piece.getRow(), piece.getCol(), -1, -1, piece.getTeam()));
            }
        });
        // help human checkbox
        helpHumanBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            helpHumanBox.setDisable(true);
            deselect();
            if(newValue) {
                if(connect(state.getScoreLimit())) {
                    helpHumanBox.setSelected(true);
                    highlightBestPieces(true);
                } else {
                    helpHumanBox.setSelected(false);
                }

            } else {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                helpHumanBox.setSelected(false);
                highlightBestPieces(false);
            }
            helpHumanBox.setDisable(false);
        });

        if (mode == HUMAN_VS_AI && playerRedInstance != HUMAN && state.getTurn() == RED) {
            aiThread = new Thread(this::doAITurn);
            aiThread.setDaemon(true);
            aiThread.start();
        }
    }

    private void doHumanTurn(Move move) {
        previousStates.add(new PrevState(state, move, turnNo));
        state = state.getNextState(move);
        turnNo++;
        if (aiRed != null) aiRed.update(state);
        if (aiBlack != null) aiBlack.update(state);

        deselect();
        playArea.update(this);
        checkGameOver();

        if (Logic.gameOver(state)) return;
        if (state.getTurn() == move.team) {
            System.out.println("TEAM " + ((move.team == RED) ? "Black" : "Red") + "'s turn has been skipped!");
            if(helpHumanBox.isSelected()) {
                highlightBestPieces(true);
            }
            return;
        }
        if (mode == HUMAN_VS_AI) {
            aiThread = new Thread(this::doAITurn);
            aiThread.setDaemon(true);
            aiThread.start();
        } else if(helpHumanBox.isSelected()) {
            highlightBestPieces(true);
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
                    if(playerRedInstance == LOOKUP_TABLE && playerBlackInstance == LOOKUP_TABLE) {
                        Thread.sleep(redTime);
                    } else {
                        Thread.sleep(0); // To allow thread interruption
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                startAIButton.setDisable(false);
                navPane.getMenuButton().setDisable(false);
                navPane.getRestartButton().setDisable(false);
            }

        });
        aiThread.setDaemon(true);
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
        turnNo++;

        if (aiRed != null) aiRed.update(state);
        if (aiBlack != null) aiBlack.update(state);
        // Update gui elements on another thread
        Platform.runLater(() -> {
            playArea.update(this);
            checkGameOver();
        });
        if (Logic.gameOver(state)) return;

        if (mode == HUMAN_VS_AI) {
            if (turn == state.getTurn()) {
                System.out.println("TEAM " + ((turn == RED) ? "Black" : "Red") + "'s turn has been skipped!");
                doAITurn();
            } else if(helpHumanBox.isSelected()) {
                highlightBestPieces(true);
            }
        }
    }

    private void checkGameOver() {
        if (Logic.gameOver(state) && !endGamePopup) {
            endGamePopup = true;
            Stage newStage = new Stage();
            int winner = Logic.getWinner(state);
            if(playerRedInstance == HUMAN) state.setTurn(RED);
            else if (playerBlackInstance == HUMAN) state.setTurn(BLACK);

            newStage.setScene(new Scene(new EndGamePane(primaryStage, winner,
                    this), 400, 150));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(window);
            newStage.setOnCloseRequest(Event::consume);
            newStage.show();
        }
    }
    public boolean connect(int scoreLimit) {
        String JDBC_URL;
        File f = new File("useAltDB");
        if (f.exists() && !f.isDirectory()) {
            JDBC_URL = "jdbc:derby:altDB;create=true";
        } else {
            JDBC_URL = "jdbc:derby:lookupDB;create=true";
        }
        
        System.out.println("Connecting to database. This might take some time");
        try {
            dbConnection = DriverManager.getConnection(
                    JDBC_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connection successful");

        String tableName = "plays_" + scoreLimit;
        long key = new Node(state).getHashCode();
        boolean error = false;
        // Try query to check for table existance
        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team, score from "
                    + tableName + " where id=" + key);
            if(!resultSet.next()) {
                System.err.println("The database table '" + tableName + "' is incomplete.");
                error = true;
            }

            statement.close();
        } catch (SQLException e) {
            System.out.println("Table '" + tableName + "' does not exist in the database!");
            error = true;
        }

        if(error) {
            showOverwritePane();
            return false;
        }
        return true;
    }

    public MinimaxPlay queryPlay(Node n) {
        MinimaxPlay play = null;
        String tableName = "plays_" + state.getScoreLimit();
        Long key = n.getHashCode();
        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team, score from "
                    + tableName + " where id=" + key);
            while(resultSet.next()) {
                Move move = new Move(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5));
                int score = resultSet.getInt(6);
                play = new MinimaxPlay(move, score, 0);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(play == null) {
            System.err.println("PLAY DOES NOT EXIST IN DATABASE!");
        }
        return play;
    }

    public String turnsToTerminal(int score) {
        if(score == 0) {
            return "∞";
        }
        if(score > 0) {
            if(state.getTurn() == BLACK) {
                return "" + (-2000 + score);
            } else {
                return "" + (2000 - score);
            }
        } else {
            if(state.getTurn() == BLACK) {
                return "" + (2000 + score);
            } else {
                return "" + (-2000 - score);
            }
        }
    }

    private void deselect() {
        if (selected != null) {
            highlightMoves(selected.getRow(), selected.getCol(), selected.getTeam(), false);
            selected.deselect();
        }
    }

    public void setSelected(BoardPiece piece) {
        deselect();
        selected = piece;
        highlightMoves(piece.getRow(), piece.getCol(), piece.getTeam(), true);
    }

    private void highlightMoves(int row, int col, int team, boolean highlight) {
        if (highlight) curHighLights = Logic.legalMovesFromPiece(row,
                col, team, state);
        ArrayList<Move> bestPlays = null;
        if(highlight && helpHumanBox.isSelected()) {
            bestPlays = bestPlays(new Node(state));
        }

        ArrayList<String> turnsToTerminalList = null;
        if(highlight && helpHumanBox.isSelected()) {
            turnsToTerminalList = getScores(curHighLights);
        }

        BoardTile[][] tiles = playArea.getBoard().getTiles();
        for (int i = 0; i < curHighLights.size(); i++) {
            Move m = curHighLights.get(i);
            String turns = "";
            if(turnsToTerminalList != null) {
                turns = turnsToTerminalList.get(i);
            }
            boolean bestMove = false;
            if(bestPlays != null && bestPlays.contains(m)) {
                bestMove = true;
            }
            if (m.newCol == -1 && m.newRow == -1) {
                if (team == RED) {
                    goalRed.setHighlight(highlight, helpHumanBox.isSelected(), bestMove, turns);
                } else {
                    goalBlack.setHighlight(highlight, helpHumanBox.isSelected(), bestMove, turns);
                }
            } else tiles[m.newRow][m.newCol].setHighlight(highlight, helpHumanBox.isSelected(), bestMove, turns);
        }
    }

    public ArrayList<Move> bestPlays(Node n) {
        ArrayList<Move> bestPlays = new ArrayList<>();
        MinimaxPlay bestPlay = queryPlay(n);
        int bestScore = 0;
        if (!Logic.gameOver(n.getNextNode(bestPlay.move).getState())) {
            bestScore = queryPlay(n.getNextNode(bestPlay.move)).score;
        }

        for (Node child : n.getChildren()) {
            Move m = child.getState().getMove();
            if(Logic.gameOver(n.getNextNode(m).getState())) {
                bestPlays.add(m);
            } else if(queryPlay(child).score == bestScore) {
                bestPlays.add(m);
            }
        }
        return bestPlays;
    }

    private void highlightBestPieces(boolean highlight) {
        Node n = new Node(state);
        ArrayList<Move> bestPlays = null;
        if (highlight) bestPlays = bestPlays(n);
        BoardTile[][] tiles = playArea.getBoard().getTiles();

        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[i].length; j++) {
                BoardPiece p = tiles[i][j].getPiece();
                if (p != null) p.setBest(false);
                if(!highlight) continue;
                for (Move m : bestPlays) {
                    if (p != null && m.oldCol == p.getCol() && m.oldRow == p.getRow()) {
                        p.setBest(true);
                    }
                }
            }
        }
        int player = state.getTurn();
        for(BoardPiece p : playArea.getPlayer(player).getPieces()) {
            p.setBest(false);
            if(!highlight) continue;
            for (Move m : bestPlays) {
                if (m.oldCol == p.getCol() && m.oldRow == p.getRow()) {
                    p.setBest(true);
                }
            }
        }
        int opponent = (player == RED) ? BLACK : RED;
        for(BoardPiece p : playArea.getPlayer(opponent).getPieces()) {
            p.setBest(false);
        }
    }

    private ArrayList<String> getScores(ArrayList<Move> curHighLights) {
        ArrayList<String> turnsToTerminalList = new ArrayList<>();
        for(Move m : curHighLights) {
            Node n = new Node(state).getNextNode(m);
            if(Logic.gameOver(n.getState())) {
                turnsToTerminalList.add("0");
            } else turnsToTerminalList.add(turnsToTerminal(queryPlay(n).score));
        }
        return turnsToTerminalList;
    }
    public State getState() {
        return state;
    }
    public int getPlayerInstance(int team) {
        if(team == RED) {
            return playerRedInstance;
        } else {
            return playerBlackInstance;
        }
    }

    private void reviewGame() {
        Stage newStage = new Stage();
        newStage.setScene(new Scene(new ReviewPane(primaryStage, this), 325, Globals.HEIGHT - 50));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(window);
        newStage.setOnCloseRequest(Event::consume);
        newStage.show();
    }
    private void showOverwritePane() {
        Stage newStage = new Stage();
        newStage.setScene(new Scene(new OverwriteDBPane(this), 500, 150));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(window);
        newStage.setOnCloseRequest(Event::consume);
        newStage.show();
    }
    public void buildDB() {
        LookupTableMinimax lt = new LookupTableMinimax(RED, state, true);
    }

    private int setMode(int playerRedInstance, int playerBlackInstance) {
        if (playerRedInstance == HUMAN && playerBlackInstance == HUMAN) {
            return HUMAN_VS_HUMAN;
        } else if (playerRedInstance != HUMAN ^ playerBlackInstance != HUMAN) {
            return HUMAN_VS_AI;
        } else {
            return AI_VS_AI;
        }
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }
    public void setPreviousStates(ArrayList<PrevState> prevStates) {
        this.previousStates = prevStates;
    }

    public BoardPiece getSelected() {
        return selected;
    }
    public int getTurnNo() {
        return turnNo;
    }
    public int getScoreLimit() {
        return state.getScoreLimit();
    }
    public int getMode() {
        return mode;
    }
    public int getTime(int team) {
        if(team == RED) return redTime;
        else return blackTime;
    }
    public boolean getOverwriteDB() {
        return overwriteDB;
    }
    public PlayArea getPlayArea() {
        return playArea;
    }
    public ArrayList<PrevState> getPreviousStates() { return previousStates; }
    public Window getWindow() { return window; }


}
