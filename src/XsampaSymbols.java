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



import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class XsampaSymbols implements Iterable<String> {

    SortedMap<String, Xsampa> symbols = new TreeMap<>();

    XsampaSymbols() {
    }

    public void add(Xsampa symbol) {
        this.symbols.put(symbol.getSymbol(), symbol);
    }

    public Xsampa getXsampa(String symbol) {
        Xsampa xsampa = new Xsampa();
        if (this.hasXsampa(symbol)) {
            xsampa = symbols.get(symbol);
        }
        return xsampa;
    }

    public Boolean hasXsampa(String symbol) {
        return symbols.containsKey(symbol);
    }

    public Iterator<String> iterator() {
        return symbols.keySet().iterator();
    }
}
