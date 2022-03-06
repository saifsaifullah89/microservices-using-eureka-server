package com.microservices.temporary.tempModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TemporaryCheckHistory{

    @Id
    @SequenceGenerator(
            name = "temporary_id_sequence",
            sequenceName = "temporary_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "temporary_id_sequence"
    )
    private Integer id;
    private Integer customerId;
    private Boolean isTemporary;
    private LocalDateTime createdAt;

}
