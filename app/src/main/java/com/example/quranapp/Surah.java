package com.example.quranapp;

import java.util.ArrayList;

public class Surah {
    public int number;
    public int surahNumber;
    public String surahName;
    public String englishName;

    public ArrayList<Aya> ayas;
    public Surah(int number,  int surahNumber, String surahName, String englishName, ArrayList<Aya> ayas) {
        this.number = number;
        this.ayas = ayas;
        this.surahNumber = surahNumber;
        this.surahName = surahName;
        this.englishName = englishName;
    }

    public String getSurahName(){
        return this.surahName;
    }
}