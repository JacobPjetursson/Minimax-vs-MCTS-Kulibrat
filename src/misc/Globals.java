package misc;

public class Globals {

    // TEAMS
    public static final int RED = 1;
    public static final int BLACK = 2;

    // PLAYER INSTANCES
    public static final int HUMAN = 1;
    public static final int MINIMAX = 2;
    public static final int MONTE_CARLO = 3;
    public static final int LOOKUP_TABLE = 4;
    public static final int FFT = 5;

    // GAME MODES
    public static final int HUMAN_VS_HUMAN = 1;
    public static final int HUMAN_VS_AI = 2;
    public static final int AI_VS_AI = 3;

    // WINDOW DIMENSIONS
    public static final int WIDTH = 800;
    public static final int HEIGHT = 650;

    // LEVEL OF SYMMETRY
    public static final int HREF = 1;
    public static final int VREF = 2;
    public static final int HVREF = 3;
    public static final int ROT = 4;
    public static final int HREF_ROT = 5;
    public static final int VREF_ROT = 6;
    public static final int HVREF_ROT = 7;

    // PREFERENCES / CUSTOMIZATION

    // BOARD CONFIG AND RULES FOR KULIBRAT
    public static final int bWidth = 2;
    public static final int bHeight = 2;
    public static final int piece_amount = 4;
    public static final boolean losePieces = false;

    // MISC
    public static final int SYMMETRY = VREF;
    public static final boolean CUSTOMIZABLE = false; // For debug
    public static final String JDBC_URL = "jdbc:derby:lookupDB;create=true";
    public static final String FFT_PATH = "fft.txt";
}
