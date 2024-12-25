package org.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String color;

    @Column(name = "cotton_percentage")
    private Integer cottonPercentage;




    public String toString() {
        return " носки {" +
                "id=" + id +
                ", цвет='" + color + '\'' +
                ", содержание хлопка=" + cottonPercentage +
                '}';
    }
}
