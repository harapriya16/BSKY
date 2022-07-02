package com.stl.bsky.entity.mdm;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.stl.bsky.entity.uac.StatusMaster;

@Getter
@Setter
@Entity
@Table(name = "icon_table", schema = "mdm")
public class IconDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="icon_id")
    private Long id;
    
    @Column
    private String iconType; 
    
    @Column
    private String faText;
    
    @Column(name="image",columnDefinition = "TEXT")
    private String image;
    
    @Column
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusMaster status;
}
