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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

public class PhoneGroups {

    private static String alphabet = "";
    private static String sourceFilesDir = "";
    private static Boolean doPhoneset = false;
    private static Character featureSepChar = '^';
    private static final List<String> excludeList = new ArrayList<>();
    private static Integer setMinNum = 2;
    private static Boolean keepSingletons = false;
    private static Boolean outputRepeatedGroups = false;
    private static final List<String> languages = new ArrayList<>();
    private static final List<List<String>> combinedGroups = new ArrayList<>();
    private static final Sampa sampaMappings = new Sampa();
    static XsampaSymbols xsampaSymbols = new XsampaSymbols();
    private static SortedMap<String, SortedSet<String>> groups = new TreeMap<>();

    public static void main(String[] args) throws IOException {

        loadConfig();

        String baseDir = sourceFilesDir + "/base";
        loadIpaBase(baseDir + "/x-sampa.dat");
        String vendor = alphabet.split("-")[0];

        for (String language : languages) {
            loadLanguageTable(sourceFilesDir + "/" + vendor + "/map_" + alphabet + "_" + language + ".dat", language);
        }

        for (String language : sampaMappings) {
            printGroups(language);
            if (doPhoneset) {
                printPhonesets(language);
            }
        }
    }

    private static void printGroups(String language) throws IOException {
        groups = new TreeMap<>();
        String outFile = "phonegroups_" + alphabet + "_" + language + ".txt";
        BufferedWriter out = AuxiliaryFunctions.openWrite(outFile);

        List<String> placeValues = Consonant.getPlaceValues();
        List<String> mannerValues = Consonant.getMannerValues();
        List<String> heightValues = Vowel.getHeightValues();
        List<String> backnessValues = Vowel.getBacknessValues();

        // single features

        for (String feature : sampaMappings.getAllFeatures(language)) {
            putCompoundGroup(language, feature);
        }

        // combined features

        // 1. hard-coded

        for (String placeValue : placeValues) {
            for (String mannerValue : mannerValues) {
                // place + manner
                putCompoundGroup(language, placeValue, mannerValue);
                putCompoundGroup(language, placeValue, mannerValue, "voiced");
                putCompoundGroup(language, placeValue, mannerValue, "unvoiced");
            }
            // place + voicing
            putCompoundGroup(language, placeValue, "voiced");
            putCompoundGroup(language, placeValue, "unvoiced");
        }
        for (String mannerValue : mannerValues) {
            // manner + voicing
            putCompoundGroup(language, mannerValue, "voiced");
            putCompoundGroup(language, mannerValue, "unvoiced");
        }
        for (String heightValue : heightValues) {
            for (String backnessValue : backnessValues) {
                // height + backness
                putCompoundGroup(language, heightValue, backnessValue);
            }
        }

        // 2. configuration-specific
        for (List<String> c : combinedGroups) {
            if (c.size() <= 3) {
                String[] combinedGroup = c.toArray(new String[0]);
                putCompoundGroup(language, combinedGroup);
            } else {
                System.err.println("Only combinations upto 3 features supported: " + c);
                System.exit(1);
            }
        }

        // output final results
        for (String feature : groups.keySet()) {
            String featureStr = feature;
            SortedSet<String> group = groups.get(feature);
            // make sure that every group has setMinNum or 1, if this allowed
            if (group.size() >= setMinNum || (group.size() == 1 && keepSingletons)) {
                // remove trailing "_" used for better sorting
                if (featureStr.endsWith("_"))
                    featureStr = feature.substring(0, feature.length() - 1);
                if (featureStr.endsWith("_*")) {
                    featureStr = feature.substring(0, feature.length() - 2) + "*";
                }
                out.write(featureStr + "\t" + group + "\n");
            }
        }
        out.close();
    }

    private static void printPhonesets(String language) throws IOException {
        String phonesetFile = "phoneset_" + alphabet + "_" + language + ".txt";
        BufferedWriter phoneset = AuxiliaryFunctions.openWrite(phonesetFile);
        for (String sampa : sampaMappings.getsampaSymbols(language)) {
            List<String> features = sampaMappings.getFeatures(language, sampa);
            // clean the features set to make friendlier in the context of a phoneset
            if (features.contains("diphthong")) {
                features.remove("vowel");
            }
            if (features.contains("complex")) {
                features.remove("consonant");
            }
            features.remove("affricate-or-double-articulation");
            phoneset.write(sampa + " ".repeat(Math.max(0, (10 - sampa.length()))) + features + "\n");
        }
        phoneset.close();
    }

    private static void putCompoundGroup(String language, String... features) {
        StringBuilder chain = new StringBuilder();
        for (String feature : features)
            chain.append(feature).append(featureSepChar);
        chain = new StringBuilder(chain.substring(0, chain.length() - 1)); // remove trailing "^"
        Map<String, SortedSet<String>> sampaSymbols = sampaMappings.getSymbolsWithFeatures(language, features);
        for (String side : sampaSymbols.keySet()) {
            boolean repeatedGroup = false;
            String repeatedSign = "";
            SortedSet<String> symbols = sampaSymbols.get(side);
            if (!symbols.isEmpty()) {
                if (groups.containsValue(symbols)) {
                    repeatedSign = "*";
                    repeatedGroup = true;
                }
                if (!repeatedGroup || outputRepeatedGroups)
                    groups.put(chain + side + repeatedSign, symbols);
            }
        }
    }

    private static void loadConfig() throws IOException {
        BufferedReader settings = AuxiliaryFunctions.openRead("config.cfg");
        String line;
        while ((line = settings.readLine()) != null) {
            Matcher setting = AuxiliaryFunctions.match("^\\s*SOURCE_FILES_DIR\\s*=\\s*\"([^\"]+)\"", line);
            if (setting.find()) {
                sourceFilesDir = setting.group(1);
            }
            setting = AuxiliaryFunctions.match("^\\s*ALPHABET\\s*=\\s*\"([^\"]+)\"", line);
            if (setting.find()) {
                alphabet = setting.group(1);
            }
            setting = AuxiliaryFunctions.match("^\\s*LANGUAGES\\s*=\\s*\"([^\"]+)\"", line);
            if (setting.find()) {
                for (String language : setting.group(1).split(",")) {
                    languages.add(language.trim());
                }
            }
            setting = AuxiliaryFunctions.match("^\\s*GROUP\\s*=\\s*\"([^\"]+)\"", line);
            if (setting.find()) {
                List<String> combinedGroup = new ArrayList<>();
                for (String group : setting.group(1).split(",")) {
                    combinedGroup.add(group.trim());
                }
                combinedGroups.add(combinedGroup);
            }
            setting = AuxiliaryFunctions.match("^\\s*DO_PHONESET\\s*=\\s*([01])", line);
            if (setting.find()) {
                if (Integer.parseInt(setting.group(1)) == 1)
                    doPhoneset = true;
            }
            setting = AuxiliaryFunctions.match("^\\s*SET_MIN_NUMBER\\s*=\\s*(\\d)", line);
            if (setting.find()) {
                setMinNum = Integer.parseInt(setting.group(1));
            }

            setting = AuxiliaryFunctions.match("^\\s*KEEP_SINGLETONS\\s*=\\s*([01])", line);
            if (setting.find()) {
                if (Integer.parseInt(setting.group(1)) == 1)
                    keepSingletons = true;
            }
            setting = AuxiliaryFunctions.match("^\\s*OUTPUT_REPEATED_GROUPS\\s*=\\s*([01])", line);
            if (setting.find()) {
                if (Integer.parseInt(setting.group(1)) == 1)
                    outputRepeatedGroups = true;
            }
            setting = AuxiliaryFunctions.match("^\\s*FEATURE_SEPCHAR\\s*=\\s*\"([^\"])\"", line);
            if (setting.find()) {
                featureSepChar = setting.group(1).charAt(0);
            }
            setting = AuxiliaryFunctions.match("^\\s*EXCLUDE\\s*=\\s*\"([^\"]+)\"", line);
            if (setting.find()) {
                excludeList.add(setting.group(1));
            }
        }
    }

    private static void loadIpaBase(String file) throws IOException {
        BufferedReader ipaBase = AuxiliaryFunctions.openRead(file);
        String line;
        while ((line = ipaBase.readLine()) != null) {
            Matcher cons = AuxiliaryFunctions.match(getPropPat(3, 0), line);
            Matcher vow1 = AuxiliaryFunctions.match(getPropPat(3, 1), line);
            Matcher vow2 = AuxiliaryFunctions.match(getPropPat(2, 1), line);
            Matcher supra = AuxiliaryFunctions.match(getPropPat(1, 2), line);
            Matcher dia = AuxiliaryFunctions.match(getPropPat(1, 3), line);
            if (cons.find()) {
                Consonant c = new Consonant(cons.group(1), //
                        Integer.parseInt(cons.group(2)), //
                        Integer.parseInt(cons.group(3)), //
                        Integer.parseInt(cons.group(4)));
                xsampaSymbols.add(c);
            } else if (vow1.find()) {
                Vowel v = new Vowel(vow1.group(1), //
                        Integer.parseInt(vow1.group(2)), //
                        Integer.parseInt(vow1.group(3)), //
                        Integer.parseInt(vow1.group(4)));
                xsampaSymbols.add(v);
            } else if (vow2.find()) {
                Vowel v = new Vowel(vow2.group(1), //
                        Integer.parseInt(vow2.group(2)), //
                        Integer.parseInt(vow2.group(3)));
                xsampaSymbols.add(v);
            } else if (supra.find()) {
                Suprasegmental s = new Suprasegmental(supra.group(1), //
                        Integer.parseInt(supra.group(2)));
                xsampaSymbols.add(s);
            } else if (dia.find()) {
                Diacritic d = new Diacritic(dia.group(1), //
                        Integer.parseInt(dia.group(2)));
                xsampaSymbols.add(d);
            }
        }
    }

    private static String getPropPat(Integer propNumber, Integer symType) {
        String sym = ":SYM\\s*[\"']([^\\s]+)[\"']";
        String type = ":PROP\\s*type\\s*=\\s*";
        String prop = "[^\\s=]+\\s*=\\s*(\\d+)";
        String pat = "^\\s*" + sym + "\\s*" + type + symType;
        switch (propNumber) {
            case 3 -> pat += ",\\s*" + prop + ",\\s*" + prop + ",\\s*" + prop;
            case 2 -> pat += ",\\s*" + prop + ",\\s*" + prop;
            case 1 -> pat += ",\\s*" + prop;
        }
        return pat;
    }

    private static void loadLanguageTable(String file, String language) throws IOException {
        BufferedReader table = AuxiliaryFunctions.openRead(file);
        String line;
        while ((line = table.readLine()) != null) {
            String sampaStr = ":MAP\\s*[\"']([^\\s]+)[\"']";
            String xsampaStr = ":TO\\s*([\"'][^!\n]+).*$";
            Matcher m = AuxiliaryFunctions.match("^\\s*" + sampaStr + "\\s*" + xsampaStr, line);
            if (m.find()) {
                String sampa = m.group(1);
                String xsampaSeq = m.group(2).trim();
                if (!excludeList.contains(sampa)) {
                    sampaMappings.addSymbol(language, sampa, xsampaSeq);
                }
            }
        }
    }
}
