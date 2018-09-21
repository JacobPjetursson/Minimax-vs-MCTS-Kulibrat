package FFT;

import ai.AI;
import game.Logic;
import game.Move;
import game.State;

public class FFT_Follower extends AI {
    private FFT fft;

    public FFT_Follower(int team, FFT fft) {
        super(team);
        this.fft = fft;
    }



    public Move makeMove(State state) {

        for (RuleGroup ruleGroup : fft.ruleGroups) {
            for (Rule rule : ruleGroup.rules) {
                //System.out.println(rule.move.oldRow + " " + rule.move.oldRow + " " + rule.move.newRow + " " + rule.move.newCol + " " + rule.move.team);
                if (rule.applies(state)) {
                    rule.move.team = state.getTurn();

                    if (Logic.isLegalMove(state, rule.move))
                        return rule.move;
                    else {
                        System.out.println("Rule applied but move was illegal. Trying next case in FFT");
                    }
                }
            }
        }
        System.out.print("No rules could be applied with a legal move. ");
        return null;
    }
}
