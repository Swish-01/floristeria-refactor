package com.floristeria.blomst.dto.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class Web {
    private Long id;
    private Long createdBy;
    private Long updatedBy;
    private String url;
    private String name;
    private String urlLogo;
}
