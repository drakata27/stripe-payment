package online.aleksdraka.stripepayment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private BigDecimal amount;
    private Long quantity;
    private String name;
    private String currency;
}
