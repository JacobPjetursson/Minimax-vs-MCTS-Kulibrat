package misc;

public class Globals {

    // TEAMS
    public static final int RED = 1;
    public static final int BLACK = 2;

    // PLAYER INSTANCES
    public static final int HUMAN = 3;
    public static final int MINIMAX = 4;
    public static final int MONTE_CARLO = 5;
    public static final int LOOKUP_TABLE = 6;

    // GAME MODES
    public static final int HUMAN_VS_HUMAN = 6;
    public static final int HUMAN_VS_AI = 7;
    public static final int AI_VS_AI = 8;

    // WINDOW DIMENSIONS
    public static final int WIDTH = 800;
    public static final int HEIGHT = 650;

    // BOARD CONFIG AND RULES
    public static final int bWidth = 3;
    public static final int bHeight = 4;
    public static final int piece_amount = 4;
    public static final boolean losePieces = false;

    // MISC
    public static final boolean CUSTOMIZABLE = false; // For debug
    public static final String JDBC_URL = "jdbc:derby:altDB;create=true";
}
