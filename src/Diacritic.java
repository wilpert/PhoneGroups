import java.util.ArrayList;
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Copyright 2014 Yandex LLC
// All Rights Reserved.
//
// Author : Alexis Wilpert



import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Diacritic extends Xsampa {

    private String mode = null;

    private static final Map<Integer, String> modeValues = new TreeMap<>();

    static {
        modeValues.put(0, "rounded");
        modeValues.put(1, "long");
        modeValues.put(2, "voiced");
        modeValues.put(3, "voiceless");
        modeValues.put(4, "breathy-voice");
        modeValues.put(5, "creaky-voice");
        modeValues.put(6, "syllabic");
        modeValues.put(7, "non-syllabic");
        modeValues.put(8, "aspirated");
        modeValues.put(9, "nasal-release");
        modeValues.put(10, "lateral-release");
        modeValues.put(11, "no-audible-release");
        modeValues.put(12, "dental");
        modeValues.put(13, "apical");
        modeValues.put(14, "laminal");
        modeValues.put(15, "linguolabial");
        modeValues.put(16, "advanced");
        modeValues.put(17, "retracted");
        modeValues.put(18, "centralized");
        modeValues.put(19, "mid-centralized");
        modeValues.put(20, "raised");
        modeValues.put(21, "lowered");
        modeValues.put(22, "nasalized");
        modeValues.put(23, "rhotacized");
        modeValues.put(24, "labialized");
        modeValues.put(25, "palatalized");
        modeValues.put(26, "velarized");
        modeValues.put(27, "pharyngealized");
        modeValues.put(28, "velarized-or-pharyngealized");
        modeValues.put(29, "more-rounded");
        modeValues.put(30, "less-rounded");
        modeValues.put(31, "ejective");
        modeValues.put(32, "advanced-tongue-root");
        modeValues.put(33, "retracted-tongue-root");
    }

    public Diacritic(String symbol, Integer mode) {
        super(symbol);
        super.isDiacritic = true;
        if (!modeValues.containsKey(mode)) {
            System.err.println("Mode " + mode + " not defined for " + symbol);
        } else {
            this.mode = modeValues.get(mode);
        }
    }

    public String getSymbol() {
        return (super.symbol);
    }

    public String getMode() {
        return (this.mode);
    }

    public List<String> getProperties() {
        List<String> properties = new ArrayList<>();
        properties.add(getMode());
        return properties;
    }

    public Boolean isDiacritic() {
        return true;
    }
}
