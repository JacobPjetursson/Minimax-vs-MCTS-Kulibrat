package FFT;

import java.util.ArrayList;

public class FFT {
    ArrayList<RuleGroup> ruleGroups;

    public FFT() {
        // Try loading fft from file in working directory

        // If fail, make new empty rulegrp
        ruleGroups = new ArrayList<RuleGroup>();
    }

    void addRuleGroup(RuleGroup ruleGroup) {
        ruleGroups.add(ruleGroup);
        save();
    }

    private void save() {

    }

    private void load(String path) {

    }
}
