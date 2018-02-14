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



import java.util.*;

public class Vowel extends Xsampa {

    private String height = null;
    private String backness = null;
    private SortedSet<String> mode = new TreeSet<String>();

    private static final Map<Integer, String> heightValues = new TreeMap<Integer, String>();
    private static final Map<Integer, String> backnessValues = new TreeMap<Integer, String>();
    private static final Map<Integer, String> modeValues = new TreeMap<Integer, String>();

    static {
        heightValues.put(0, "open");
        heightValues.put(1, "near-open");
        heightValues.put(2, "open-mid");
        heightValues.put(3, "mid");
        heightValues.put(4, "close-mid");
        heightValues.put(5, "near-close");
        heightValues.put(6, "close");

        backnessValues.put(0, "front");
        backnessValues.put(1, "near-front");
        backnessValues.put(2, "central");
        backnessValues.put(3, "near-back");
        backnessValues.put(4, "back");

        modeValues.put(0, "rounded");
        modeValues.put(23, "rhotacized");
    }

    public Vowel(String symbol, Integer height, Integer backness) {
        super(symbol);
        super.isVowel = true;
        this.height = heightValues.get(height);
        this.backness = backnessValues.get(backness);
    }

    public Vowel(String symbol, Integer height, Integer backness, Integer mode) {
        super(symbol);
        this.isVowel = true;
        this.height = heightValues.get(height);
        this.backness = backnessValues.get(backness);
        if (!modeValues.containsKey(mode)) {
            System.err.println("Mode " + mode + " not defined for " + symbol);
        } else {
            this.mode.add(modeValues.get(mode));
        }
    }

    public String getSymbol() {
        return (super.symbol);
    }

    public String getHeight() {
        return (this.height);
    }

    public String getBackness() {
        return (this.backness);
    }

    public SortedSet<String> getMode() {
        SortedSet<String> mode = new TreeSet<String>();
        if (this.mode != null) {
            mode = this.mode;
        }
        return mode;
    }

    public List<String> getProperties() {
        List<String> properties = new ArrayList<String>();
        properties.add(getHeight());
        properties.add(getBackness());
        if (mode != null) {
            for (String mode : getMode()) {
                properties.add(mode);
            }
        }
        return properties;
    }

    public Boolean isVowel() {
        return true;
    }

    public static List<String> getHeightValues() {
        List<String> heightValues = new ArrayList<String>();
        for (String value : Vowel.heightValues.values()) {
            heightValues.add(value);
        }
        return heightValues;
    }

    public static List<String> getBacknessValues() {
        List<String> backnessValues = new ArrayList<String>();
        for (String value : Vowel.backnessValues.values()) {
            backnessValues.add(value);
        }
        return backnessValues;
    }
}
