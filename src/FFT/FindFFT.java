package FFT;

import game.State;


public class FindFFT {
    public static void main(String args[]) {
        State state = new State(1);
        FFTMinimax minimax = new FFTMinimax(1);
        minimax.makeFFT(state);

    }
}
