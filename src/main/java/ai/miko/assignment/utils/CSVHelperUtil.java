package ai.miko.assignment.utils;

import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CSVHelperUtil {
    public boolean isExist(String fileName, String dbName) {
        return Files.exists(Paths.get(dbName, fileName + ".csv"));
    }

    @SneakyThrows
    public void saveToCSV(List<String> row, String fileName, String dbName) {
        Files.createDirectories(Paths.get(dbName) );
        CSVPrinter printer = new CSVPrinter(new FileWriter(String.valueOf(Paths.get(dbName, fileName + ".csv")), true), CSVFormat.DEFAULT);
        printer.printRecord(row);
        printer.close();
    }

    public List<List<String>> readFileToList(String name, String defaultDb) {

        List<List<String>> data = new ArrayList<>();
        try {
            Files.lines(Paths.get(defaultDb, name + ".csv"))
                    .map(line -> Arrays.asList(line.split(",")))
                    .forEach(data::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


}
