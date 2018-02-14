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

public class Consonant extends Xsampa {

    private String place = null;
    private String manner = null;
    private SortedSet<String> mode = new TreeSet<String>();

    private static final Map<Integer, String> placeValues = new TreeMap<Integer, String>();
    private static final Map<Integer, String> mannerValues = new TreeMap<Integer, String>();
    private static final Map<Integer, String> modeValues = new TreeMap<Integer, String>();

    static {

        placeValues.put(0, "bilabial");
        placeValues.put(1, "labial-velar");
        placeValues.put(2, "labiodental");
        placeValues.put(3, "dental");
        placeValues.put(4, "alveolar");
        placeValues.put(5, "postalveolar");
        placeValues.put(6, "palato-alveolar");
        placeValues.put(7, "alveolo-palatal");
        placeValues.put(8, "retroflex");
        placeValues.put(9, "palatal");
        placeValues.put(10, "labial-palatal");
        placeValues.put(11, "velar");
        placeValues.put(12, "uvular");
        placeValues.put(13, "pharyngeal");
        placeValues.put(14, "epiglottal");
        placeValues.put(15, "glottal");

        mannerValues.put(0, "click");
        mannerValues.put(1, "lateral-click");
        mannerValues.put(2, "plosive");
        mannerValues.put(3, "ejective");
        mannerValues.put(4, "implosive");
        mannerValues.put(5, "fricative");
        mannerValues.put(6, "lateral-fricative");
        mannerValues.put(7, "nasal");
        mannerValues.put(8, "tap-flap");
        mannerValues.put(9, "lateral-flap");
        mannerValues.put(10, "trill");
        mannerValues.put(11, "approximant");
        mannerValues.put(12, "lateral-approximant");
        mannerValues.put(13, "simultaneous-S-and-x");

        modeValues.put(2, "voiced");
        modeValues.put(3, "unvoiced");
    }

    public Consonant(String symbol, Integer place, Integer manner, Integer mode) {
        super(symbol);
        super.isConsonant = true;
        this.place = placeValues.get(place);
        this.manner = mannerValues.get(manner);
        if (!modeValues.containsKey(mode)) {
            System.err.println("Mode " + mode + " not defined for " + symbol);
        } else {
            this.mode.add(modeValues.get(mode));
        }
    }

    public String getSymbol() {
        return (super.symbol);
    }

    public String getPlace() {
        return (this.place);
    }

    public String getManner() {
        return (this.manner);
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
        properties.add(getPlace());
        properties.add(getManner());
        if (mode != null) {
            for (String mode : getMode()) {
                properties.add(mode);
            }
        }
        return properties;
    }

    public Boolean isConsonant() {
        return true;
    }

    public static List<String> getPlaceValues() {
        List<String> placeValues = new ArrayList<String>();
        for (String value : Consonant.placeValues.values()) {
            placeValues.add(value);
        }
        return placeValues;
    }

    public static List<String> getMannerValues() {
        List<String> mannerValues = new ArrayList<String>();
        for (String value : Consonant.mannerValues.values()) {
            mannerValues.add(value);
        }
        return mannerValues;
    }
}
