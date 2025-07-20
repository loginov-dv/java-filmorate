package ru.yandex.practicum.filmorate.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// TypeAdapter для преобразования LocalDate в String
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    // Форматтер для LocalDate
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDate.format(dtf));
        }
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString(), dtf);
    }
}