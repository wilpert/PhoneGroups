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



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Suprasegmental extends Xsampa {

	private String id = null;

	private static final Map<Integer, String> idValues = new TreeMap<>();

	static {
		idValues.put(0, "primary-stress");
		idValues.put(1, "secondary-stress");
		idValues.put(2, "half-long");
		idValues.put(3, "extra-short");
		idValues.put(4, "syllable-break");
		idValues.put(5, "minor-group");
		idValues.put(6, "major-group");
		idValues.put(7, "linking");
		idValues.put(8, "affricate-or-double-articulation");
		idValues.put(9, "global-rise");
		idValues.put(10, "global-fall");
		idValues.put(11, "extra-high-level");
		idValues.put(12, "high-level");
		idValues.put(13, "mid-level");
		idValues.put(14, "low-level");
		idValues.put(15, "extra-low-level");
		idValues.put(16, "downstep");
		idValues.put(17, "upstep");
		idValues.put(18, "rising-contour");
		idValues.put(19, "falling-contour");
		idValues.put(20, "word-boundary");
	}

	public Suprasegmental(String symbol, Integer id) {
		super(symbol);
		super.isSuprasegmental = true;
		if (!idValues.containsKey(id)) {
			System.err.println("Id " + id + " not defined for " + symbol);
		} else {
			this.id = idValues.get(id);
		}
	}

	public String getSymbol() {
		return (super.symbol);
	}

	public String getId() {
		return (this.id);
	}

	public List<String> getProperties() {
		List<String> properties = new ArrayList<>();
		properties.add(getId());
		return properties;
	}

	public Boolean isSuprasegmental() {
		return true;
	}
}
