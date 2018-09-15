package FFT;

import misc.Globals;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FFT {
    String path = Globals.FFT_PATH;
    ArrayList<RuleGroup> ruleGroups;

    public FFT() {
        ruleGroups = new ArrayList<RuleGroup>();
        // Try loading fft from file in working directory
        load();

    }

    void addRuleGroup(RuleGroup ruleGroup) {
        ruleGroups.add(ruleGroup);
        save();
    }

    void save() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            String fft_file = "";
            for (RuleGroup rg : ruleGroups) {
                fft_file += "[" + rg.name + "]\n";
                for (Rule r : rg.rules) {
                    fft_file += r.getClauseStr() + " -> " + r.getActionStr() + "\n";
                }
            }
            writer.write(fft_file);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void load() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path));
            RuleGroup rg = null;
            for (String line : lines) {
                // Rulegroup name
                if (line.startsWith("[")) {
                    if (rg != null) {
                        ruleGroups.add(rg);
                    }
                    rg = new RuleGroup(line.substring(1, line.length() - 1));
                } else {
                    String[] rule = line.split("->");
                    String clausesStr = rule[0].trim();
                    String actionStr = rule[1].trim();
                    if (rg != null) {
                        rg.rules.add(new Rule(clausesStr, actionStr));
                    }
                }
            }
            if(rg != null)
                ruleGroups.add(rg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
