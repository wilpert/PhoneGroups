# PhoneGroups

## General

The Java application PhoneGroups generates phonetic groups from the phonetic definitions contained in subdir "tables" based on the phonetic features defined in "tables/x-sampa.dat". The configuration file "config.cfg" can be tuned to produce different results. Please read it before running the application.

Phonetic groups or classes are useful, for example, for training acoustic models for speech recognition. But I think they can be also useful by themselves, as an additional source of information (or rather perspective) on a given phoneme set.

The application expects the configuration file to be named "config.cfg" and to be in the same directory as the running application. The phoneme tables should be located also in the same directory (with the structure "tables/VENDOR/TABLE").

## Format description

```
REPEATED_SYMBOL = '*'
CONTEXT = '_L' | '_R'
SIMPLE_GROUP = phonetic_feature
COMPLEX_GROUP = SIMPLEGROUP ( '_' SIMPLE_GROUP )*
GROUP = ( SIMPLE_GROUP | COMPLEX_GROUP ) [ REPEATED_SYMBOL ] [ CONTEXT ]
SYMBOLS = '[' SYMBOL ',' SYMBOL ( ',' SYMBOL )* ']'
PHONE_GROUP = GROUP '\t' SYMBOLS
```

## Notes

* If a group has the repeated symbol '*', the same set of symbols appears at least once under a different set of phonetic features.
* The context suffixes '_L' and '_R' are used when the group contains complex symbols (diphthongs, affricates mostly).
