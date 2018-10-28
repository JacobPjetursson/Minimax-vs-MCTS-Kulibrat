
package FFT;

import misc.Config;

public class Clause extends FFTLib.FFT.Clause {
    public int boardWidth = Config.bWidth;
    public int boardHeight = Config.bHeight;



    Clause(int row, int col, int pieceOcc, boolean negation) {
        super(row, col, pieceOcc, negation);
    }

    Clause(String name) {
        super(name);
    }

    Clause(Clause duplicate) {
        super(duplicate);
    }
}
