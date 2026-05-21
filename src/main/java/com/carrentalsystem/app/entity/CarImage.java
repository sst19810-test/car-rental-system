package com.carrentalsystem.app.entity;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    @NotNull
    private Car car;
}
