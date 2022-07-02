package com.stl.bsky.entity.registration;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "attachments", schema = "registration")
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String docPath;

    private String docFormat;

    private Long docTypeId;

    private Long tabTypeId;

    private Long refId;

    private String originalName;
}
