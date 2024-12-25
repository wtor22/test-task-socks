package org.example.dto;

import lombok.Data;

@Data
public class StockDto {

    private SockDto sockDto;
    private int quantity;
    private Integer cottonPercentage;
}
