# Project Description and Setup

This project is about solving the board game called Kulibrat.

It does so by brute-forcing with Minimax.

Three different AI's are implemented.

The first agent is a Minimax algorithm with iterative deepening, alpha beta pruning and transposition tables.

The second agent is an MCTS algorithm.

The third agent is cheating, since it just looks up the best move from a local database.
The database stores all possible states from the current game configuration and links them to the best respective play.
The DB is not uploaded to Git due to its size. It is generated, if wished, when starting the game, and is then stored on disk for future use.

The project can be compiled as is. There is also an executable JAR-file which launches the project instantly. The database and library files must be in the same folder as the JAR-file.

# How to play the game

When starting a new game, choose who should be playing as Red and Black

It is possible to change the computation time for each move for MCTS and Minimax.

For the lookup table, there is an option to overwrite the database, which is necessary for the perfect player to work. 

Once in-game, the human player can ask for help by the perfect player, granted that the database has been built for the chosen score limit. This will highlight the best moves in green, and all other moves in red.
The numbers shown on the tiles say how many turns it takes to win (for positive number) or lose (for negative number), for that child state. This is assuming perfect play from opponent.

During or after the game, it is possible to review the game, when playing vs. the AI. This will show the player all the moves that was made during the game, and if they were perfect or not.
It also gives the option to go back to a state of free choice and play the game from there.