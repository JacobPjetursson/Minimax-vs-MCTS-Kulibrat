# Minimax vs. MCTS

This project implements three different AI types for the board game called Kulibrat.

The first agent is a Minimax algorithm with iterative deepening, alpha beta pruning and transposition tables.

The second agent is an MCTS algorithm.

These are combined in different ways and tested against each other. 

The third agent is cheating, since it just looks up the best move from a local database.
The database stores all possible states from the current game configuration and links them to the best respective play.
The DB is not uploaded to Git due to its size. It is generated, if wished, when starting the project, and is then stored for future use.

The project can be compiled as is. There is also an executable JAR-file which launches the project instantly. For that to work properly, the database and library files must be in the same folder as the JAR-file.
