package FFT;

import java.util.ArrayList;

public class RuleGroup {
    ArrayList<Rule> rules;
    String name;

    public RuleGroup(String name) {
        rules = new ArrayList<Rule>();
        this.name = name;
    }

    public RuleGroup(String name, ArrayList<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }

    public RuleGroup(RuleGroup copy) {
        this.rules = new ArrayList<Rule>(copy.rules);
        this.name = copy.name;
    }
}
