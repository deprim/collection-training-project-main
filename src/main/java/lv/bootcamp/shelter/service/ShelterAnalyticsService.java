package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.*;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals()
                .stream()
                .sorted(Comparator.comparing(Animal::getName))
                .toList();

        Set<String> uniqueSpecies = allAnimals.stream()
                .map(Animal::getSpecies)
                .collect(Collectors.toCollection(TreeSet::new));


        Map<String, List<Animal>> animalsBySpecies = allAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies));


        List<String> animalsNeedingVetInput = allAnimals.stream()
                .filter(animal -> animal.getAge() == null)
                .map(animal -> animal.getName() + "(" + animal.getSpecies() + ")")
                .toList();

        Map<String, Long> vaccinatedPerSpecies = allAnimals.stream()
                .filter(Animal::isVaccinated)
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        Map<String, Long> unvaccinatedPerSpecies = allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        Map<String, Animal> oldestAnimalPerSpecies = allAnimals.stream()
                .filter(animal -> animal.getAge() != null)
                .collect(Collectors.toMap(Animal::getSpecies,
                        animal -> animal,
                        (a, b) -> a.getAge() > b.getAge() ? a : b));


        return new ShelterReportData(importResult,
                uniqueSpecies,
                animalsBySpecies,
                animalsNeedingVetInput,
                vaccinatedPerSpecies,
                unvaccinatedPerSpecies,
                oldestAnimalPerSpecies);
    }
}
