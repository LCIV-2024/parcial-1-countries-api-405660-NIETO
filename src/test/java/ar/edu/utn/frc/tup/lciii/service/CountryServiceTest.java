package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.clients.dtos.CountryDto;
import ar.edu.utn.frc.tup.lciii.clients.dtos.CountryRequest;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CountryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private List<Map<String, Object>> mockCountries;

    @BeforeEach
    public void setUp() {
        mockCountries = List.of(
                Map.of(
                        "name", Map.of("common", "Country 1"),
                        "cca3", "C1",
                        "population", 1000000L,
                        "area", 50000.0
                ),
                Map.of(
                        "name", Map.of("common", "Country 2"),
                        "cca3", "C2",
                        "population", 2000000L,
                        "area", 100000.0
                )
        );
    }

    @Test
    public void testSave() {
        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class)).thenReturn(mockCountries);

        List<CountryEntity> savedCountries = List.of(
                CountryEntity.builder().name("Country 1").code("C1").population(1000000L).area(50000.0).build(),
                CountryEntity.builder().name("Country 2").code("C2").population(2000000L).area(100000.0).build()
        );

        when(countryRepository.saveAll(savedCountries)).thenReturn(savedCountries);

        List<CountryEntity> result = countryService.saveCountries(2);


        assertEquals(2, result.size());
        //assertEquals("Country 1", result.get(0).getName());
        //assertEquals("Country 2", result.get(1).getName());
    }

    @Test
    public void testGetAllCountriesAsDto() {
        List<Map<String, Object>> mockResponse = Arrays.asList(
                createMockCountrys("AR", "Argentina"),
                createMockCountrys("BR", "Brazil")
        );

        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class))
                .thenReturn(mockResponse);

        List<CountryDto> result = countryService.getAllCountriesAsDto();

        assertEquals(2, result.size());
        assertEquals("Argentina", result.get(0).getName());
        assertEquals("Brazil", result.get(1).getName());
    }

    private Map<String, Object> createMockCountrys(String code, String name) {
        Map<String, Object> country = new HashMap<>();
        country.put("cca2", code);
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("common", name);
        country.put("name", nameMap);
        return country;
    }

    @Test
    void testGetCountriesByCriteria_withName() {
        List<Map<String, Object>> mockCountries = Arrays.asList(
                createMockCountry("AR", "Argentina"),
                createMockCountry("BR", "Brazil"),
                createMockCountry("CL", "Chile")
        );
        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class))
                .thenReturn(mockCountries);

        List<CountryDto> result = countryService.getCountriesByCriteria(null, "Brazil");

        assertEquals(1, result.size());
    }

    @Test
    void testGetCountriesByCriteria_noCriteria() {
        // Arrange
        List<Map<String, Object>> mockCountries = Arrays.asList(
                createMockCountry("AR", "Argentina"),
                createMockCountry("BR", "Brazil"),
                createMockCountry("CL", "Chile")
        );
        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class))
                .thenReturn(mockCountries);

        // Act
        List<CountryDto> result = countryService.getCountriesByCriteria(null, null);

        // Assert
        assertEquals(3, result.size()); // Debería devolver todos los países simulados
    }

    private Map<String, Object> createMockCountry(String code, String name) {
        Map<String, Object> country = new HashMap<>();
        country.put("cca2", code);
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("common", name);
        country.put("name", nameMap);
        return country;
    }
}