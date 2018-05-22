package game;

public class PrevState {
    private State state;
    private Move move;
    private int turnNo;

    public PrevState(State state, Move move, int turnNo) {
        this.state = state;
        this.move = move;
        this.turnNo = turnNo;
    }

    public int getTurnNo() {
        return turnNo;
    }

    public Move getMove() {
        return move;
    }

    public State getState() {
        return state;
    }
}
