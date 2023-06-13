package com.fisheep.geocalcu.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@Entity
@Table(name = "kindergarten")
@NoArgsConstructor
@AllArgsConstructor
public class KinderGarten {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String address;

    private String administrativeCode;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @Transient
    private Double distance;
}
