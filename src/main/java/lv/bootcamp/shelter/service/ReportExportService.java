package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) throws IOException {



        try (BufferedWriter bw = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            bw.write("=== Shelter Intake Report ===");
            bw.newLine();
            bw.write("Date generated: " + LocalDate.now());
            bw.newLine();
            bw.newLine();
            bw.write("Total imported: " + reportData.importResult().importedRows());
            bw.newLine();
            bw.write("Total skipped: " + reportData.importResult().skippedRows());
            bw.newLine();
            bw.newLine();
            bw.write("--- Unique species ---");
            bw.newLine();

            bw.write(reportData.uniqueSpecies().stream()
                    .collect(Collectors.joining(", ")));

            bw.newLine();
            bw.newLine();
            bw.write("--- Per-species breakdown ---");
            bw.newLine();
            for (String species : reportData.uniqueSpecies()) {
                int total = reportData.animalsBySpecies().get(species).size();
                long vaccinated = reportData.vaccinatedPerSpecies().getOrDefault(species, 0L);
                bw.write(species + ": " + total + " total, " + vaccinated + " vaccinated");
                bw.newLine();
            }

            bw.newLine();

            bw.write("--- Oldest animal per species ---");
            bw.newLine();
            for (String species : reportData.uniqueSpecies()) {
                Animal oldest = reportData.oldestAnimalBySpecies().get(species);
                if (oldest == null) {
                    bw.write(species + ": No animals with known age");
                    bw.newLine();
                    continue;
                }
                bw.write(species + ": " + oldest.getName() + " (age " + oldest.getAge() + ")");
                bw.newLine();
            }

            bw.newLine();
            bw.write("--- Animals Vet input to determine age ---");
            bw.newLine();
            for (String animal : reportData.animalsNeedingVetInput()) {
                bw.write(animal);
                bw.newLine();
            }

        }

    }
}
