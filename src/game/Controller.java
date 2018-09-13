package game;

import FFT.*;
import ai.AI;
import ai.MCTS.MCTS;
import ai.Minimax.*;
import gui.*;
import gui.board.BoardPiece;
import gui.board.BoardTile;
import gui.board.Goal;
import gui.board.Player;
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

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import static misc.Globals.*;

public class Controller {
    public Connection dbConnection;
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
    private Button editFFTButton;
    private CheckBox interactiveFFTBox;
    private Button[] swapButtons;
    private CheckBox helpHumanBox;
    private Thread aiThread;
    private NavPane navPane;
    private BoardPiece selected;
    private ArrayList<Move> curHighLights;
    public Stage primaryStage;
    private State state;
    private PlayArea playArea;
    private Goal goalRed;
    private Goal goalBlack;
    private boolean endGamePopup;
    private ArrayList<PrevState> previousStates;
    private Window window;
    private FFT fft;

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

        instantiateAI(RED);
        instantiateAI(BLACK);

        // Fetch all gui elements that invoke something game-related
        startAIButton = navPane.getStartAIButton();
        stopAIButton = navPane.getStopAIButton();
        helpHumanBox = navPane.getHelpHumanBox();
        reviewButton = navPane.getReviewButton();
        editFFTButton = navPane.getEditFFTButton();
        interactiveFFTBox = navPane.getInteractiveFFTBox();
        goalRed = playArea.getGoal(RED);
        goalBlack = playArea.getGoal(BLACK);

        showNavButtons();

        // Set event handlers for gui elements
        // Swap player buttons
        swapButtons = new Button[2];
        Player[] players = new Player[2];
        players[0] = playArea.getPlayer(RED);
        players[1] = playArea.getPlayer(BLACK);
        swapButtons[0] = players[0].getSwapBtn();
        swapButtons[1] = players[1].getSwapBtn();
        for (int i = 0; i < swapButtons.length; i++) {
            Player p = players[i];
            Button b = swapButtons[i];
            b.setOnMouseClicked(event -> {
                deselect();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(new SwapPlayerPane(this, p), 325, 400));
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.initOwner(window);
                newStage.setOnCloseRequest(Event::consume);
                newStage.show();
            });
        }


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
            if (connect(state.getScoreLimit())) {
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
            if (newValue) {
                if (connect(state.getScoreLimit())) {
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
        // FFT LISTENERS
        // edit fft button
        editFFTButton.setOnMouseClicked(event -> {
            deselect();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(new EditFFTPane(this, fft), 450, Globals.HEIGHT - 50));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(window);
            newStage.setOnCloseRequest(Event::consume);
            newStage.show();
        });

        // interactive mode
        interactiveFFTBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            deselect();
            if (newValue) {

            } else {

            }
        });

        if (mode == HUMAN_VS_AI && playerRedInstance != HUMAN && state.getTurn() == RED) {
            aiThread = new Thread(this::doAITurn);
            aiThread.setDaemon(true);
            aiThread.start();
        }
    }

    private void showNavButtons() {
        navPane.removeWidgets();
        if (mode == AI_VS_AI && !navPane.containsAIWidgets())
            navPane.addAIWidgets();
        if (mode == HUMAN_VS_AI && !navPane.containsReviewButton())
            navPane.addReviewButton();
        if (mode != AI_VS_AI && !navPane.containsHelpBox())
            navPane.addHelpHumanBox();
        if ((playerBlackInstance == FFT || playerRedInstance == FFT) && !navPane.containsFFTWidgets())
            navPane.addFFTWidgets();
    }

    private void instantiateAI(int team) {
        if (team == RED) {
            if (playerRedInstance == MINIMAX) {
                aiRed = new Minimax(RED, redTime);
            } else if (playerRedInstance == LOOKUP_TABLE) {
                aiRed = new LookupTableMinimax(RED, state, overwriteDB);
            } else if (playerRedInstance == MONTE_CARLO) {
                aiRed = new MCTS(state, RED, redTime);
            } else if (playerRedInstance == FFT) {
                fft = new FFT();
                aiRed = new FFT_Follower(RED, fft);
            }
        } else {
            if (playerBlackInstance == MINIMAX) {
                aiBlack = new Minimax(BLACK, blackTime);
            } else if (playerBlackInstance == LOOKUP_TABLE) {
                if (playerRedInstance == LOOKUP_TABLE) {
                    overwriteDB = false;
                }
                aiBlack = new LookupTableMinimax(BLACK, state, overwriteDB);
            } else if (playerBlackInstance == MONTE_CARLO) {
                aiBlack = new MCTS(state, BLACK, blackTime);
            } else if (playerBlackInstance == FFT) {
                fft = new FFT();
                aiBlack = new FFT_Follower(BLACK, fft);
            }
        }
    }
    // Is called when a tile is pressed by the user. If vs. the AI, it calls the doAITurn after. This function also highlights
    // the best pieces for the opponent, if it is human vs human.
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
            String skipped = (state.getTurn() == RED) ? "Black" : "Red";
            System.out.println("TEAM " + skipped + "'s turn has been skipped!");
            playArea.getInfoPane().displaySkippedTurn(skipped);
            if (helpHumanBox.isSelected()) {
                highlightBestPieces(true);
            }
            return;
        }
        if (mode == HUMAN_VS_AI) {
            aiThread = new Thread(this::doAITurn);
            aiThread.setDaemon(true);
            aiThread.start();
        } else if (helpHumanBox.isSelected()) {
            highlightBestPieces(true);
        }
    }

    // This function is called when two AI's are matched against each other. It can be interrupted by the user.
    // For the lookup table, a delay can be set
    private void startAI() {
        // For the AI vs. AI mode. New thread is needed to update the gui while running the AI
        navPane.getRestartButton().setDisable(true);
        navPane.getMenuButton().setDisable(true);
        startAIButton.setDisable(true);
        for (Button b : swapButtons)
            b.setDisable(true);

        aiThread = new Thread(() -> {
            try {
                while (!Logic.gameOver(state)) {
                    doAITurn();
                    if (playerRedInstance == LOOKUP_TABLE && playerBlackInstance == LOOKUP_TABLE) {
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
                for (Button b : swapButtons)
                    b.setDisable(false);
            }
        });
        aiThread.setDaemon(true);
        aiThread.start();
    }

    // The AI makes its turn, and the GUI is updated while doing so
    private void doAITurn() {
        int turn = state.getTurn();
        Move move;
        if (aiRed != null && turn == RED) {
            move = aiRed.makeMove(state);
            if (playerRedInstance == FFT && move == null)
                move = getDefaultFFTMove();
        } else {
            move = aiBlack.makeMove(state);
            if (playerBlackInstance == FFT && move == null)
                move = getDefaultFFTMove();
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
                String skipped = (turn == RED) ? "Black" : "Red";
                System.out.println("TEAM " + skipped + "'s turn has been skipped!");
                playArea.getInfoPane().displaySkippedTurn(skipped);
                doAITurn();
            } else if (helpHumanBox.isSelected()) {
                highlightBestPieces(true);
            }
        }
    }

    private Move getDefaultFFTMove() {
        Random r = new Random();
        int moveSize = state.getLegalMoves().size();
        int index = r.nextInt(moveSize);
        return state.getLegalMoves().get(index);
    }

    // Checks if the game is over and shows a popup. Popup allows a restart, go to menu, or review game
    private void checkGameOver() {
        if (Logic.gameOver(state) && !endGamePopup) {
            endGamePopup = true;
            Stage newStage = new Stage();
            int winner = Logic.getWinner(state);
            if (playerRedInstance == HUMAN) state.setTurn(RED);
            else if (playerBlackInstance == HUMAN) state.setTurn(BLACK);

            newStage.setScene(new Scene(new EndGamePane(primaryStage, winner,
                    this), 400, 150));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(window);
            newStage.setOnCloseRequest(Event::consume);
            newStage.show();
        }
    }

    // Connects to the database. If the table in question is incomplete or missing, show a pane to allow creating the DB on the spot.
    public boolean connect(int scoreLimit) {
        String JDBC_URL = Globals.JDBC_URL;
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
            if (!resultSet.next()) {
                System.err.println("The database table '" + tableName + "' is incomplete.");
                error = true;
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Table '" + tableName + "' does not exist in the database!");
            error = true;
        }
        if (error) {
            showOverwritePane();
            return false;
        }
        return true;
    }

    // Fetches the best play corresponding to the input node
    public MinimaxPlay queryPlay(Node n) {
        MinimaxPlay play = null;
        String tableName = "plays_" + state.getScoreLimit();
        Long key = n.getHashCode();
        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team, score from "
                    + tableName + " where id=" + key);
            while (resultSet.next()) {
                Move move = new Move(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5));
                int score = resultSet.getInt(6);
                play = new MinimaxPlay(move, score, 0);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (play == null) {
            System.err.println("PLAY DOES NOT EXIST IN DATABASE!");
        }
        return play;
    }

    // Outputs a string which is the amount of turns to a terminal node, based on a score from the database entry
    public String turnsToTerminal(int score) {
        if (score == 0) {
            return "∞";
        }
        if (score > 0) {
            if (state.getTurn() == BLACK) {
                return "" + (-2000 + score);
            } else {
                return "" + (2000 - score);
            }
        } else {
            if (state.getTurn() == BLACK) {
                return "" + (2000 + score);
            } else {
                return "" + (-2000 - score);
            }
        }
    }

    // Deselects the selected piece
    public void deselect() {
        if (selected != null) {
            highlightMoves(selected.getRow(), selected.getCol(), selected.getTeam(), false);
            selected.deselect();
            selected = null;
        }
    }

    // Shows the red/green/yellow highlight on the tiles when a piece has been selected
    private void highlightMoves(int row, int col, int team, boolean highlight) {
        if (highlight) curHighLights = Logic.legalMovesFromPiece(row,
                col, team, state);
        ArrayList<Move> bestPlays = null;
        if (highlight && helpHumanBox.isSelected()) {
            bestPlays = bestPlays(new Node(state));
        }
        ArrayList<String> turnsToTerminalList = null;
        if (highlight && helpHumanBox.isSelected()) {
            turnsToTerminalList = getScores(curHighLights);
        }
        BoardTile[][] tiles = playArea.getBoard().getTiles();
        for (int i = 0; i < curHighLights.size(); i++) {
            Move m = curHighLights.get(i);
            String turns = "";
            if (turnsToTerminalList != null) {
                turns = turnsToTerminalList.get(i);
            }
            boolean bestMove = false;
            if (bestPlays != null && bestPlays.contains(m)) {
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

    // Outputs a list of the best plays from a given node. Checks through the children of a node to find the ones
    // which have the least amount of turns to terminal for win, or most for loss.
    public ArrayList<Move> bestPlays(Node n) {
        ArrayList<Move> bestPlays = new ArrayList<>();
        MinimaxPlay bestPlay = queryPlay(n);
        int bestScore = 0;
        if (!Logic.gameOver(n.getNextNode(bestPlay.move).getState())) {
            bestScore = queryPlay(n.getNextNode(bestPlay.move)).score;
        }
        for (Node child : n.getChildren()) {
            Move m = child.getState().getMove();
            State state = n.getNextNode(m).getState();
            if (Logic.gameOver(state)) {
                if (Logic.getWinner(state) == m.team) bestPlays.add(m);
            } else if (queryPlay(child).score == bestScore) {
                bestPlays.add(m);
            }
        }
        return bestPlays;
    }

    // Highlights the best pieces found above
    private void highlightBestPieces(boolean highlight) {
        Node n = new Node(state);
        ArrayList<Move> bestPlays = null;
        if (highlight) bestPlays = bestPlays(n);
        BoardTile[][] tiles = playArea.getBoard().getTiles();

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                BoardPiece p = tiles[i][j].getPiece();
                if (p != null) p.setBest(false);
                if (!highlight) continue;
                for (Move m : bestPlays) {
                    if (p != null && m.oldCol == p.getCol() && m.oldRow == p.getRow()) {
                        p.setBest(true);
                    }
                }
            }
        }
        int player = state.getTurn();
        for (BoardPiece p : playArea.getPlayer(player).getPieces()) {
            p.setBest(false);
            if (!highlight) continue;
            for (Move m : bestPlays) {
                if (m.oldCol == p.getCol() && m.oldRow == p.getRow()) {
                    p.setBest(true);
                }
            }
        }
        int opponent = (player == RED) ? BLACK : RED;
        for (BoardPiece p : playArea.getPlayer(opponent).getPieces()) {
            p.setBest(false);
        }
    }

    // Adds sting scores to all moves from a piece
    private ArrayList<String> getScores(ArrayList<Move> curHighLights) {
        ArrayList<String> turnsToTerminalList = new ArrayList<>();
        for (Move m : curHighLights) {
            Node n = new Node(state).getNextNode(m);
            if (Logic.gameOver(n.getState())) {
                turnsToTerminalList.add("0");
            } else turnsToTerminalList.add(turnsToTerminal(queryPlay(n).score));
        }
        return turnsToTerminalList;
    }

    public State getState() {
        return state;
    }

    public int getPlayerInstance(int team) {
        if (team == RED) {
            return playerRedInstance;
        } else {
            return playerBlackInstance;
        }
    }

    public void setPlayerInstance(int team, int playerInstance) {
        if (team == RED) {
            playerRedInstance = playerInstance;
        } else {
            playerBlackInstance = playerInstance;
        }
        int oldMode = mode;
        this.mode = setMode(playerRedInstance, playerBlackInstance);
        instantiateAI(team);
        if (state.getTurn() == team && playerInstance != Globals.HUMAN && mode != AI_VS_AI) {
            doAITurn();
        } else if (state.getTurn() != team && oldMode == AI_VS_AI && mode != AI_VS_AI) {
            doAITurn();
        }
        showNavButtons();
        playArea.update(this);
    }

    // Opens the review pane
    private void reviewGame() {
        Stage newStage = new Stage();
        newStage.setScene(new Scene(new ReviewPane(primaryStage, this), 325, Globals.HEIGHT - 50));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(window);
        newStage.setOnCloseRequest(Event::consume);
        newStage.show();
    }

    // Opens the overwrite pane for DB
    private void showOverwritePane() {
        Stage newStage = new Stage();
        newStage.setScene(new Scene(new OverwriteDBPane(this), 500, 150));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(window);
        newStage.setOnCloseRequest(Event::consume);
        newStage.show();
    }

    // Builds the DB
    public void buildDB() {
        LookupTableMinimax lt = new LookupTableMinimax(RED, state, true);
    }

    // Sets the mode based on the red and black player types
    private int setMode(int playerRedInstance, int playerBlackInstance) {
        if (playerRedInstance == HUMAN && playerBlackInstance == HUMAN) {
            return HUMAN_VS_HUMAN;
        } else if (playerRedInstance != HUMAN ^ playerBlackInstance != HUMAN) {
            return HUMAN_VS_AI;
        } else {
            return AI_VS_AI;
        }
    }

    public BoardPiece getSelected() {
        return selected;
    }

    public void setSelected(BoardPiece piece) {
        deselect();
        selected = piece;
        highlightMoves(piece.getRow(), piece.getCol(), piece.getTeam(), true);
    }

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public int getScoreLimit() {
        return state.getScoreLimit();
    }

    public int getMode() {
        return mode;
    }

    public int getTime(int team) {
        if (team == RED) return redTime;
        else return blackTime;
    }

    public boolean getOverwriteDB() {
        return overwriteDB;
    }

    public void setOverwriteDB(boolean overwrite) {
        this.overwriteDB = overwrite;
    }

    public void setPlayerCalcTime(int team, int time) {
        if (team == RED)
            redTime = time;
        else
            blackTime = time;
    }

    public PlayArea getPlayArea() {
        return playArea;
    }

    public ArrayList<PrevState> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(ArrayList<PrevState> prevStates) {
        this.previousStates = prevStates;
    }

    public Window getWindow() {
        return window;
    }

}
