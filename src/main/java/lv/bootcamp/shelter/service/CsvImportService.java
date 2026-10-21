package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeParseException;

@Slf4j
public class CsvImportService {

    public ImportResult importAnimals(Path inputPath) throws IOException {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();
        int importedRows = 0;
        int skippedRows = 0;


        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            String header = reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length < 5) {
                    log.warn("Skipping malformed row: {}", line);
                    skippedRows++;
                    continue;
                }

                String name = parts[0].strip();
                String species = parts[1].strip();

                if (name.isBlank() || species.isBlank()) {
                    log.warn("Skipping malformed row: {}", line);
                    skippedRows++;
                    continue;
                }


                String ageString = parts[2].strip();
                Integer age;
                try {
                    age = ageString.isBlank() ? null : Integer.parseInt(ageString);
                } catch (NumberFormatException e) {
                    log.warn("Skipping malformed row: {}", line);
                    skippedRows++;
                    continue;
                }

                boolean vaccinated = Boolean.parseBoolean(parts[3].strip());

                LocalDate intakeDate = null;
                try {
                    intakeDate = LocalDate.parse(
                            parts[4].strip(),
                            DateTimeFormatter.ofPattern("dd.MM.uuuu")
                                    .withResolverStyle(ResolverStyle.STRICT));
                } catch (DateTimeParseException e) {
                    log.warn("Skipping malformed row: {}", line);
                    skippedRows++;
                    continue;
                }

                allAnimals.add(new Animal(name, species, age, vaccinated, intakeDate));
                importedRows++;

            }

        }

        return new ImportResult(allAnimals, skippedRows, importedRows);
    }
}
