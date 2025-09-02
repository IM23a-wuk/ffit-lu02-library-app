package ch.bzz.io;

import ch.bzz.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TsvBookReader {

    public List<Book> read(Path path) throws IOException {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            Map<String, Integer> headerMap = null;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (headerMap == null) {
                    headerMap = parseHeader(line);
                    continue;
                }

                Book book = parseBook(line, lineNumber, headerMap);
                if (book != null) {
                    books.add(book);
                }
            }
        }
        return books;
    }

    private Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> headerMap = new HashMap<>();
        String[] headers = headerLine.split("\t");
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim().toLowerCase();
            switch (header) {
                case "id":
                case "title":
                case "author":
                case "isbn":
                    headerMap.put(header, i);
                    break;
                case "publishedyear":
                case "year":
                    headerMap.put("year", i);
                    break;
                default:
                    System.err.println("Warning: Unknown column '" + headers[i] + "' in TSV header. It will be ignored.");
                    break;
            }
        }
        return headerMap;
    }

    private Book parseBook(String line, int lineNumber, Map<String, Integer> headerMap) {
        String[] fields = line.split("\t", -1);

        Long id = getLongValue(fields, headerMap, "id", lineNumber);
        String title = getStringValue(fields, headerMap, "title");

        if (id == null) {
            System.err.println("Warning: Skipping line " + lineNumber + " because 'id' is missing or invalid.");
            return null;
        }
        if (title == null) {
            System.err.println("Warning: Skipping line " + lineNumber + " because 'title' is missing or empty.");
            return null;
        }

        String author = getStringValue(fields, headerMap, "author");
        String isbn = getStringValue(fields, headerMap, "isbn");
        Integer year = getIntValue(fields, headerMap, "year", lineNumber);

        return new Book(id, isbn, title, author, year != null ? year : 0);
    }
    
    private String getStringValue(String[] fields, Map<String, Integer> headerMap, String key) {
        Integer index = headerMap.get(key);
        if (index != null && index < fields.length) {
            String value = fields[index].trim();
            return value.isEmpty() ? null : value;
        }
        return null;
    }

    private Long getLongValue(String[] fields, Map<String, Integer> headerMap, String key, int lineNumber) {
        String valueStr = getStringValue(fields, headerMap, key);
        if (valueStr == null) {
            return null;
        }
        try {
            return Long.parseLong(valueStr);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Invalid number format for '" + key + "' on line " + lineNumber + ". Value: '" + valueStr + "'.");
            return null;
        }
    }

    private Integer getIntValue(String[] fields, Map<String, Integer> headerMap, String key, int lineNumber) {
        String valueStr = getStringValue(fields, headerMap, key);
        if (valueStr == null) {
            return null;
        }
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Invalid number format for '" + key + "' on line " + lineNumber + ". Value: '" + valueStr + "'.");
            return null;
        }
    }
}
