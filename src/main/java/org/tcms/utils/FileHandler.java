package org.tcms.utils;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.nio.file.*;
import java.util.*;


public class FileHandler {
    private Path path;
    private List<String> headers;

    public FileHandler(String fileName, List<String> headerCols) throws IOException {
        this.path = Paths.get("src/main/resources/entity", fileName);
        this.headers = new ArrayList<>(headerCols);
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {
                    writer.writeNext(headers.toArray(new String[0]));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not init CSV file", e);
        }
    }

    /** Read all rows into a List of Maps (header→value). */
    public List<Map<String, String>> readAll() {
        try (CSVReader reader = new CSVReader(new FileReader(path.toFile()))) {
            List<Map<String, String>> rows = new ArrayList<>();
            String[] headerLine = reader.readNext(); // Skip header
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < headerLine.length; i++) {
                    String key = headerLine[i];
                    String value = (i < nextLine.length) ? nextLine[i] : "";
                    rowMap.put(key, value);
                }
                rows.add(rowMap);
            }
            return rows;
        } catch (IOException | CsvValidationException e) {
            throw new UncheckedIOException("Could not read CSV file", (IOException) e);
        }
    }

    /** Append a single row (missing keys become empty). */
    public void append(Map<String, String> row) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile(), true))) {
            String[] values = new String[headers.size()];
            for (int i = 0; i < headers.size(); i++) {
                values[i] = row.getOrDefault(headers.get(i), "");
            }
            writer.writeNext(values);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not append to CSV file", e);
        }
    }

    /** Overwrite entire file (header + all rows). */
    public void overwriteAll(List<Map<String, String>> rows) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {
            // Write header first
            writer.writeNext(headers.toArray(new String[0]));

            // Write all rows
            for (Map<String, String> row : rows) {
                String[] values = new String[headers.size()];
                for (int i = 0; i < headers.size(); i++) {
                    values[i] = row.getOrDefault(headers.get(i), "");
                }
                writer.writeNext(values);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not overwrite CSV file", e);
        }
    }
}