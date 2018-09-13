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
}
