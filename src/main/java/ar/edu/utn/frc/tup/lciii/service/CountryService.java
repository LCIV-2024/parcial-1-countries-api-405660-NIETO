package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.clients.dtos.CountryDto;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

        @Autowired
        private final CountryRepository countryRepository;

        @Autowired
        private final RestTemplate restTemplate;

        public List<Country> getAllCountries() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
                return response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }

        /**
         * Agregar mapeo de campo cca3 (String)
         * Agregar mapeo campos borders ((List<String>))
         */
        private Country mapToCountry(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");

                String cca3 = (String) countryData.get("cca3");

                return Country.builder()
                        .name((String) nameData.get("common"))
                        .population(((Number) countryData.get("population")).longValue())
                        .area(((Number) countryData.get("area")).doubleValue())
                        .region((String) countryData.get("region"))
                        .languages((Map<String, String>) countryData.get("languages"))
                        .code(cca3)
                        .build();
        }

        // 1
        public List<CountryDto> getAllCountriesAsDto() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                return response.stream()
                        .map(this::mapToCountryDto)
                        .collect(Collectors.toList());
        }

        // 2
        private CountryDto mapToCountryDto(Map<String, Object> countryData) {
                String code = (String) countryData.get("cca3");
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                String name = (String) nameData.get("common");

                return new CountryDto(code, name);
        }

        // 3
        public List<CountryDto> getCountriesByCriteria(String code, String name) {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                List<CountryDto> countries = response.stream()
                        .map(this::mapToCountryDto)
                        .collect(Collectors.toList());

                if (code != null && !code.isEmpty()) {
                        countries = countries.stream()
                                .filter(country -> country.getCode().equalsIgnoreCase(code))
                                .collect(Collectors.toList());
                }

                if (name != null && !name.isEmpty()) {
                        countries = countries.stream()
                                .filter(country -> country.getName().equalsIgnoreCase(name))
                                .collect(Collectors.toList());
                }

                return countries;
        }

        // 4
        public List<CountryDto> getCountriesByContinent(String continent) {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                return response.stream()
                        .filter(country -> {
                                List<String> continents = (List<String>) country.get("continents");
                                return continents != null && continents.contains(continent);
                        })
                        .map(this::mapToCountryDto)
                        .collect(Collectors.toList());
        }

        // 4
        public List<CountryDto> getCountriesByLanguage(String language) {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                return response.stream()
                        .filter(country -> {
                                Map<String, String> languages = (Map<String, String>) country.get("languages");
                                return languages != null && languages.containsValue(language);
                        })
                        .map(this::mapToCountryDto)
                        .collect(Collectors.toList());
        }

        // 6
        public List<CountryDto> getCountriesWithMostBorders() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                return response.stream()
                        .filter(country -> country.get("borders") != null)
                        .sorted((c1, c2) -> {
                                List<String> borders1 = (List<String>) c1.get("borders");
                                List<String> borders2 = (List<String>) c2.get("borders");
                                return Integer.compare(borders2.size(), borders1.size());
                        })
                        .limit(5)
                        .map(this::mapToCountryDto)
                        .collect(Collectors.toList());
        }

        // 7
        public List<CountryEntity> saveCountries(int amountOfCountryToSave) {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                List<Map<String, Object>> selectedCountries = response.stream()
                        .limit(Math.min(amountOfCountryToSave, 10))
                        .collect(Collectors.toList());

                Collections.shuffle(selectedCountries);

                List<CountryEntity> countriesToSave = selectedCountries.stream()
                        .map(this::mapToCountryEntity)
                        .collect(Collectors.toList());

                return countryRepository.saveAll(countriesToSave);
        }

        private CountryEntity mapToCountryEntity(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                String name = (String) nameData.get("common");
                String code = (String) countryData.get("cca3");
                Long population = ((Number) countryData.get("population")).longValue();
                Double area = ((Number) countryData.get("area")).doubleValue();

                return CountryEntity.builder()
                        .name(name)
                        .code(code)
                        .population(population)
                        .area(area)
                        .build();
        }

        private CountryDto mapToDTO(Country country) {
                return new CountryDto(country.getCode(), country.getName());
        }
}