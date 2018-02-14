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

public class Sampa implements Iterable<String> {

    // language --> [ sampa --> [ features ] ]
    private static SortedMap<String, SortedMap<String, List<String>>> symbols = //
            new TreeMap<String, SortedMap<String, List<String>>>();

    public Sampa() {
    }

    public void addSymbol(String language, String sampa, String xsampaSeq) {
        SortedMap<String, List<String>> mapping = new TreeMap<String, List<String>>();
        if (symbols.containsKey(language)) {
            mapping = symbols.get(language);
        }
        mapping.put(sampa, xsampaSeqToFeatures(xsampaSeq));
        symbols.put(language, mapping);
    }

    public List<String> getFeatures(String language, String sampa) {
        List<String> features = new ArrayList<String>();
        try {
            SortedMap<String, List<String>> mapping = symbols.get(language);
            try {
                features = mapping.get(sampa);
            } catch (Exception e) {
                System.err.println("No X-SAMPA features found for " + sampa + " in " + language);
            }
        } catch (Exception e) {
            System.err.println("Language " + language + " not loaded");
        }
        return features;
    }

    public SortedSet<String> getAllFeatures(String language) {
        SortedSet<String> features = new TreeSet<String>();
        try {
            SortedMap<String, List<String>> mapping = symbols.get(language);
            for (String sampa : this.getsampaSymbols(language)) {
                features.addAll(mapping.get(sampa));
            }
        } catch (Exception e) {
            System.err.println("Language " + language + " not loaded");
        }
        return features;
    }

    public Map<String, SortedSet<String>> getSymbolsWithFeatures(String language, String... featuresToSearch) {
        Map<String, SortedSet<String>> symbolsWithSides = new TreeMap<String, SortedSet<String>>();
        SortedSet<String> symbolsL = new TreeSet<String>();
        SortedSet<String> symbolsR = new TreeSet<String>();
        SortedSet<String> symbolsLR = new TreeSet<String>();
        for (String sampa : this.getsampaSymbols(language)) {
            List<String> features = this.getFeatures(language, sampa);
            if (featuresToSearch.length == 1) {
                String feature = featuresToSearch[0];
                if (!feature.equals("SEP")) {
                    // the features below are present only once in "SEP" symbols; the following
                    // check makes sure that the corresponding groups are included
                    if (features.contains(feature) && (feature.equals("complex") || feature.equals("diphthong"))) {
                        symbolsLR.add(sampa);
                    } else if (features.contains("SEP")) { // we are dealing with a diphthong or a complex
                        // feature "vowel" is not set in the right part of the diphthong
                        if (feature.equals("vowel") && features.contains("diphthong")) {
                            symbolsLR.add(sampa);
                            // feature "consonant" is not set in the right part of the complex
                        } else if (feature.equals("consonant") && features.contains("complex")) {
                            symbolsLR.add(sampa);
                        } else {
                            List<String> featuresLeft = features.subList(0, features.indexOf("SEP"));
                            List<String> featuresRight = features.subList(features.indexOf("SEP") + 1, features.size());
                            // left has feat OR right has feat
                            if (featuresLeft.contains(feature) || featuresRight.contains(feature)) {
                                if (featuresLeft.contains(feature))
                                    symbolsL.add(sampa);
                                if (featuresRight.contains(feature))
                                    symbolsR.add(sampa);
                            }
                            if (featuresLeft.contains(feature) && featuresRight.contains(feature)) {
                                symbolsLR.add(sampa);
                            }
                        }
                        // we are dealing now with simple phones (no diphthong or complex)
                    } else if (features.contains(feature)) {
                        symbolsLR.add(sampa);
                        // vowels and consonants should appear only in basic group
                        if (!feature.equals("vowel") && !feature.equals("consonant")) {
                            symbolsR.add(sampa);
                            symbolsL.add(sampa);
                        }
                    }
                } // !feature.equals("SEP")
            } else { // 2 or three features to search
                // handle the cases of diphthongs and affricates
                // They should be included in a group only when:
                // - both parts share the same feature,
                // - or depending only on the feature of one part.
                // That is, for examples, that the following group should be avoided:
                // alveolar^fricative [d_Z, s, t_S,z]
                if (features.contains("SEP")) { // we are dealing with a diphthong or an affricate
                    List<String> featuresLeft = features.subList(0, features.indexOf("SEP"));
                    List<String> featuresRight = features.subList(features.indexOf("SEP") + 1, features.size() - 1);
                    // left has feat1 and feat2 OR right has feat1 and feat2
                    if ((containsAll(featuresLeft, featuresToSearch) || containsAll(featuresRight, featuresToSearch))
                            // left has feat1 and feat2 AND right has feat1 and feat2
                            || (containsAll(featuresLeft, featuresToSearch) && containsAll(featuresRight,
                            featuresToSearch))) {
                        if (containsAll(featuresLeft, featuresToSearch))
                            symbolsL.add(sampa);
                        if (containsAll(featuresRight, featuresToSearch))
                            symbolsR.add(sampa);
                        if (containsAll(featuresLeft, featuresToSearch) && containsAll(featuresRight, featuresToSearch))
                            symbolsLR.add(sampa);
                    }
                } else if (containsAll(features, featuresToSearch)) {
                    symbolsLR.add(sampa);
                    symbolsR.add(sampa);
                    symbolsL.add(sampa);
                }
            }
        }

        // add "_" to sort the groups together: plosive, plosive_L, plosive_R
        // the trailing "_" is removed before the final output
        symbolsWithSides.put("_", symbolsLR);
        if (!(symbolsL.equals(symbolsR) && symbolsR.equals(symbolsLR))) {
            symbolsWithSides.put("_L", symbolsL);
            symbolsWithSides.put("_R", symbolsR);
        }
        return symbolsWithSides;
    }

    private static Boolean containsAll(List<String> list, String[] features) {
        Boolean res = true;
        for (String f : features) {
            if (!list.contains(f)) {
                res = false;
                break;
            }
        }
        return res;
    }

    public Iterator<String> iterator() {
        return symbols.keySet().iterator();
    }

    public Set<String> getsampaSymbols(String language) {
        return (symbols.get(language)).keySet();
    }

    private static List<String> xsampaSeqToFeatures(String xsampa) {
        List<String> features;
        Boolean seqStarted = false;
        String sym = "";
        Character delimiter = null;
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < xsampa.length(); i++) {
            if (xsampa.charAt(i) != ' ') {
                Character ch = xsampa.charAt(i);
                if (ch == '"' || ch == '\'') {
                    if (!seqStarted) {
                        seqStarted = true;
                        delimiter = ch;
                    } else { // sequence started
                        if (ch != delimiter) {
                            sym += ch;
                        } else {
                            tokens.add(sym);
                            seqStarted = false;
                            sym = "";
                        }
                    }
                } else {
                    sym += ch;
                }
            }
        }
        features = extractFeatures(tokens);
        return features;
    }

    private static List<String> extractFeatures(List<String> tokens) {
        List<String> features = new ArrayList<String>();
        Xsampa xsampaToken;
        Boolean typeSet = false;
        Boolean isAffricate = false;
        for (String token : tokens) {
            xsampaToken = PhoneGroups.xsampaSymbols.getXsampa(token);
            if (xsampaToken.isVowel()) {
                if (typeSet) {
                    features.add(0, "diphthong");
                    features.add("SEP");
                } else {
                    features.add(0, "vowel");
                    typeSet = true;
                }
            } else if (xsampaToken.isConsonant()) {
                if (typeSet) {
                    features.add(0, "complex");
                    features.add("SEP");
                    isAffricate = true;
                } else {
                    features.add(0, "consonant");
                    typeSet = true;
                }
            }
            if (token.equals(":") && isAffricate) {
                features.add(features.indexOf("SEP") - 1, "long");
            }
            try {
                for (String property : xsampaToken.getProperties()) {
                    features.add(property);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] getting properties for token: " + token);
            }
        }
        // delete feature "non-syllabic" if we are dealing with a diphtong
        if (features.contains("diphthong")) {
            features.remove("non-syllabic");
        }
        // delete feature "affricate-or-double-articulation"
        if (features.contains("complex") || features.contains("diphthong")) {
            features.remove("affricate-or-double-articulation");
        }
        return features;
    }
}
