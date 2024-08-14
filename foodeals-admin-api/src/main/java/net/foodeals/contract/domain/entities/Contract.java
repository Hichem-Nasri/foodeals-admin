package net.foodeals.contract.domain.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

@Entity
@Table(name = "contracts")

@Getter
@Setter
public class Contract extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String content;
}
