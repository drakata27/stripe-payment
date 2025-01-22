package online.aleksdraka.stripepayment.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private BigDecimal amount;
    private Long quantity;
    private String name;
    private String description;
    private String currency;
}
