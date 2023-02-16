package com.example.quranapp;

import java.util.ArrayList;
import java.util.List;

public class Parah {
    public int juz;
    public String value;
    public List<Surah> surah;
    public Parah(int juz, String value, List<Surah> surah) {
        this.juz = juz;
        this.value = value;
        this.surah = surah;
    }

    public String getValue() {
        return value;
    }
}