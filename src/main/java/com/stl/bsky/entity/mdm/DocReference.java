package com.stl.bsky.entity.mdm;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "doc_reference", schema = "mdm")
@Entity
public class DocReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String docRef;

    @ManyToOne
    private TabReference tabReference;

}
