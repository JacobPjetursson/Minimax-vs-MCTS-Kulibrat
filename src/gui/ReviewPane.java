package gui;

import ai.Minimax.Node;
import game.Controller;
import game.Logic;
import game.PrevState;
import game.State;
import gui.board.Board;
import gui.board.Goal;
import gui.board.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class ReviewPane extends StackPane {
    private ListView<HBox> lw;
    private boolean connected;

    public ReviewPane(Stage primaryStage, Controller currCont) {
        try {
            if(currCont.dbConnection == null || currCont.dbConnection.isClosed()) {
                currCont.connect(currCont.getScoreLimit());
                connected = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttons, new Insets(10));
        Button goToState = new Button("Go to State");
        goToState.setDisable(true);
        buttons.getChildren().add(goToState);

        Button cancel = new Button("Cancel");
        buttons.getChildren().add(cancel);
        cancel.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
            if(connected) {
                try {
                    currCont.dbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(Logic.gameOver(currCont.getState())) {
                Stage newStage = new Stage();
                newStage.setScene(new Scene(new EndGamePane(primaryStage, Logic.getWinner(currCont.getState()),
                        currCont), 400, 150));
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.initOwner(currCont.getWindow());
                newStage.setOnCloseRequest(Event::consume);
                newStage.show();
            }
        });

        lw = new ListView<>();
        ObservableList<HBox> prevStateBoxes = FXCollections.observableArrayList();
        for(PrevState ps : currCont.getPreviousStates()) {
            HBox h = new HBox(25);

            PlayBox playBox = getPlayBox(currCont, ps.getState());
            Label turnL = new Label("Turn: " + (ps.getTurnNo()) );

            String moveStr = String.format("Move (row, col): (%d, %d) -> (%d, %d)",
                    ps.getMove().oldRow+1, ps.getMove().oldCol+1, ps.getMove().newRow+1, ps.getMove().newCol+1);
            Label moveL = new Label(moveStr);
            Label performance;
            if(currCont.bestPlays(new Node(ps.getState())).contains(ps.getMove())) {
                h.setStyle("-fx-background-color: rgb(0, 150, 0);");
                performance = new Label("Perfect move");
            } else  {
                performance = new Label("Imperfect move");
                h.setStyle("-fx-background-color: rgb(150,0,0);");
            }

            h.getChildren().addAll(playBox, turnL, moveL, performance);
            prevStateBoxes.add(h);
        }
        lw.setItems(prevStateBoxes);
        lw.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> goToState.setDisable(false));

        goToState.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();

            int index = lw.getSelectionModel().getSelectedIndex();
            PrevState selected = currCont.getPreviousStates().get(index);
            Controller selectedCont = new Controller(primaryStage, currCont.getPlayerInstance(RED),
                    currCont.getPlayerInstance(BLACK), selected.getState(),
                    currCont.getTime(RED), currCont.getTime(BLACK), false);
            selectedCont.setTurnNo(selected.getTurnNo());
            selectedCont.getPlayArea().update(selectedCont);

            ArrayList<PrevState> prevStates = new ArrayList<>();
            for(PrevState ps : currCont.getPreviousStates()) {
                if(ps.getTurnNo() < selectedCont.getTurnNo()) {
                    prevStates.add(ps);
                }
            }
            selectedCont.setPreviousStates(prevStates);
        });


        getChildren().addAll(lw, buttons);

    }

    private PlayBox getPlayBox(Controller cont, State s) {
        Board b = new Board(15, 5, false);
        Player playerBlack = new Player(BLACK, cont, 15, 5, false);
        Goal goalRed = new Goal(3 * b.getTileSize(), 12);
        Goal goalBlack = new Goal(3 * b.getTileSize(), 12);
        Player playerRed = new Player(RED, cont, 15, 5, false);

        PlayBox pb = new PlayBox(playerBlack, goalRed, b, goalBlack, playerRed);
        pb.update(cont, s);

        return new PlayBox(playerBlack, goalRed, b, goalBlack, playerRed);
    }
}
