package ar.edu.utn.frc.tup.lciii.clients.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CountryDto {
    private String code;
    private String name;
}
