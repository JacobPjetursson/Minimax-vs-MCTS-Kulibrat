package FFT;

import ai.AI;
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
                if (rule.applies(state))
                    return rule.move;
            }
        }
        System.out.println("Random move");
        return null;
    }
}
