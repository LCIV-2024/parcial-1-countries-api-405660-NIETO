package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.clients.dtos.CountryDto;
import ar.edu.utn.frc.tup.lciii.clients.dtos.CountryRequest;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor()
@RestController
@RequestMapping("/api")
public class CountryController {

    @Autowired
    private final CountryService countryService;

    // 1
    @GetMapping("/countries")
    public ResponseEntity<List<CountryDto>> getCountries(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name) {

        List<CountryDto> countries = countryService.getAllCountriesAsDto();

        if (code != null) {
            countries = countries.stream()
                    .filter(country -> country.getCode().equalsIgnoreCase(code))
                    .collect(Collectors.toList());
        }
        if (name != null) {
            countries = countries.stream()
                    .filter(country -> country.getName().equalsIgnoreCase(name))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(countries);
    }

    // 2
    @GetMapping("/countries/filtered") // Cambié aquí
    public ResponseEntity<List<CountryDto>> getCountriesFiltered(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name) {

        List<CountryDto> countries = countryService.getCountriesByCriteria(code, name);

        return ResponseEntity.ok(countries);
    }


    // 3
    @GetMapping("/{continent}/continent")
    public ResponseEntity<List<CountryDto>> getCountriesByContinent(@PathVariable String continent) {
        List<CountryDto> countries = countryService.getCountriesByContinent(continent);
        return ResponseEntity.ok(countries);
    }


    // 4
    @GetMapping("/countries/{language}/language")
    public ResponseEntity<List<CountryDto>> getCountriesByLanguage(@PathVariable String language) {
        List<CountryDto> countries = countryService.getCountriesByLanguage(language);
        return ResponseEntity.ok(countries);
    }

    // 6
    @GetMapping("/countries/most-borders")
    public ResponseEntity<List<CountryDto>> getCountriesWithMostBorders() {
        List<CountryDto> countries = countryService.getCountriesWithMostBorders();
        return ResponseEntity.ok(countries);
    }

    // 7
    @PostMapping("/countries")
    public ResponseEntity<List<CountryDto>> saveCountries(@RequestBody CountryRequest request) {
        List<CountryEntity> savedCountries = countryService.saveCountries(request.getAmountOfCountryToSave());

        List<CountryDto> countryDtos = savedCountries.stream()
                .map(country -> new CountryDto(country.getCode(), country.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(countryDtos);
    }

}