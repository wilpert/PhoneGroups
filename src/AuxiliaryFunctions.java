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



import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AuxiliaryFunctions {

    public static BufferedReader openRead(String fileStr) {

        InputStreamReader readFileIn = null;
        try {
            InputStream FileIn = new FileInputStream(fileStr);
            readFileIn = new InputStreamReader(FileIn, "UTF-8");
        } catch (IOException e) {
            System.err.println("\n[ERROR] Could not open: " + fileStr);
            System.exit(1);
        }
        return new BufferedReader(readFileIn);
    }

    public static BufferedWriter openWrite(String fileStr) {

        OutputStreamWriter writeFileOut = null;
        try {
            OutputStream FileOut = new FileOutputStream(fileStr);
            writeFileOut = new OutputStreamWriter(FileOut, "UTF-8");
        } catch (IOException e) {
            System.err.println("\n[ERROR] Could not open: " + fileStr);
            System.exit(1);
        }
        return new BufferedWriter(writeFileOut);
    }

    public static Matcher match(String pat, String target) {

        Pattern pattern = Pattern.compile(pat);
        return pattern.matcher(target);
    }
}
