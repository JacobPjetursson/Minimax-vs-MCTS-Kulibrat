# Minimax vs. MCTS

This project implements two different AI types for the board game called Kulibrat.

The first agent is a Minimax algorithm with iterative deepening, alpha beta pruning and transposition tables.

The second agent is an MCTS algorithm.

These are combined in different ways and tested against each other. 

A winner strategy has also been found, for each game config. A lookup-table has been generated, which stores the best move for each state in a database.

The project can be compiled as is. There is also a JAR-file for those that don't care about the code. The jar file does not work with the lookup database.
