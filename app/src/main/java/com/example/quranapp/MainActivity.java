package com.example.quranapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private List<Surah> surahList;
    private List<Parah> parahList;
    private RecyclerView recyclerView;
    private AyaAdapter ayaAdapter;
    private Spinner spinnerSurah, spinnerTranslation, spinnerParah;
    private List<String> surahNames;
    private List<Surah> filteredQuranList;
    private int parah = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        spinnerSurah = findViewById(R.id.spinnerSurah);
        spinnerParah = findViewById(R.id.spinnerParah);
        spinnerTranslation = findViewById(R.id.spinnerTranslation);

        loadQuranData();
        filteredQuranList = surahList;
        surahNames = getSurahNames(surahList);

        // set up the adapter for the spinner
        ArrayAdapter<String> parahAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getParahNames(parahList));
        parahAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParah.setAdapter(parahAdapter);

        // set up the adapter for the spinner
        ArrayAdapter<String> surahAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, surahNames);
        surahAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSurah.setAdapter(surahAdapter);

        // Set up translations
        List<String> translations = new ArrayList<>();
        translations.add("Urdu Translation");
        translations.add("English Translation");
        translations.add("Sindhi Translation");
        translations.add("Hindi Translation");
        translations.add("Pushto Translation");
        ArrayAdapter<String> translationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, translations);
        translationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTranslation.setAdapter(translationsAdapter);

        // set up the recycler view
        List<Aya> ayas = new ArrayList<>();
        AyaAdapter ayaAdapter = new AyaAdapter(ayas, 0);
        recyclerView.setAdapter(ayaAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // listen for changes in the surah selection
        spinnerSurah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // filter the quran data for the selected surah
                //filteredQuranList = getQuranDataForSurah(surahList, surahNames.get(position));
                // update the recycler view with the filtered data
                ayas.clear();
                ayas.addAll(parahList.get(parah).surah.get(position).ayas);
                ayaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
        spinnerParah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                surahAdapter.clear();
                surahAdapter.addAll(getSurahNames(parahList.get(position).surah));
                surahAdapter.notifyDataSetChanged();
                parah = position;

                ayas.clear();
                ayaAdapter.notifyDataSetChanged();
                spinnerSurah.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
        spinnerTranslation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ayaAdapter.setSelectedTranslation(position);
                ayaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private List<Surah> getQuranDataForSurah(List<Surah> quranList, String surahName) {
        // filter the quran data for the selected surah
        return quranList.stream().filter(quran -> quran.getSurahName().equals(surahName)).collect(Collectors.toList());
    }

    private List<String> getSurahNames(List<Surah> quranList) {
        // get a list of unique surah names
        return quranList.stream().map(Surah::getSurahName).distinct().collect(Collectors.toList());
    }

    private List<String> getParahNames(List<Parah> parahList) {
        // get a list of unique surah names
        return parahList.stream().map(Parah::getValue).distinct().collect(Collectors.toList());
    }

    private void loadQuranData() {
        surahList = new ArrayList<>();
        parahList = new ArrayList<>();
        surahList.add(new Surah(0, 0, "Surah Select", "Surah Select", new ArrayList<>()));
        parahList.add(new Parah(0, "Parah Select", surahList));
        List<Surah> parahSurah = new ArrayList<>();
        parahSurah.add(new Surah(0, 0, "Surah Select", "Surah Select", new ArrayList<>()));
        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream inputStream = assetManager.open("quran.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);
            int surahNumber = 1, juz = 1;
            ArrayList<Aya> ayas = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (surahNumber != jsonObject.getInt("surah_number")) {
                    JSONObject oldObject = jsonArray.getJSONObject(i - 1);
                    Surah surah = new Surah(
                            oldObject.getInt("number"),
                            oldObject.getInt("surah_number"),
                            oldObject.getString("surah_name"),
                            oldObject.getString("englishName"),
                            ayas
                    );
                    surahList.add(surah);
                    parahSurah.add(surah);
                    ayas = new ArrayList<>();
                    surahNumber += 1;
                }
                if (juz != jsonObject.getInt("juz")) {
                    JSONObject oldObject = jsonArray.getJSONObject(i - 1);
                    parahSurah.add(new Surah(
                            oldObject.getInt("number"),
                            oldObject.getInt("surah_number"),
                            oldObject.getString("surah_name"),
                            oldObject.getString("englishName"),
                            ayas
                    ));
                    parahList.add(new Parah(oldObject.getInt("juz"), oldObject.getString("juz"), parahSurah));
                    parahSurah = new ArrayList<>();
                    parahSurah.add(new Surah(0, 0, "Surah Select", "Surah Select", new ArrayList<>()));
                    juz += 1;
                }
                Aya aya = new Aya();
                aya.text = jsonObject.getString("text");
                aya.urduTranslation = jsonObject.getString("UrduTranslation");
                aya.englishTranslation = jsonObject.getString("EnglishTranslation");
                aya.sindhiTranslation = jsonObject.getString("SindhiTranslation");
                aya.pushtoTranslation = jsonObject.getString("PushtoTransation");
                aya.hindiTranslation = jsonObject.getString("HindiTranslation");
                ayas.add(aya);
                if (i == jsonArray.length() - 1) {
                    Surah surah = new Surah(jsonObject.getInt("number"), jsonObject.getInt("surah_number"), jsonObject.getString("surah_name"), jsonObject.getString("englishName"), ayas);
                    surahList.add(surah);
                }
                if (i == jsonArray.length() - 1) {
                    parahSurah.add(new Surah(
                            jsonObject.getInt("number"),
                            jsonObject.getInt("surah_number"),
                            jsonObject.getString("surah_name"),
                            jsonObject.getString("englishName"),
                            ayas
                    ));
                    parahList.add(new Parah(jsonObject.getInt("juz"), jsonObject.getString("juz"), parahSurah));
                }
            }

            //recyclerView.setAdapter(new RecyclerViewAdapter(getContext(), verseList, selectedTranslation));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}