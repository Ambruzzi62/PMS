package com.microida.pms.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PmsProduct {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column
    private String catgoryOriginal;

    @Column(length = 6000)
    private String descriptionOriginal;

    @Column(length = 6000)
    private String descriptionOwn;

    @Column
    private Integer idOriginal;

    @Column(length = 500)
    private String nameOriginal;

    @Column(length = 500)
    private String nameOwn;

    @Column(length = 500)
    private String originalUrl;

    @Column
    private Double priceOriginal;

    @Column
    private String priceOwn;

    @Column
    private String sellerName;

    @Column
    private Double shippingPrice;

    @Column
    private String currency;

    @Column
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PmsCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "description_id")
    private PmsDescription description;

    @OneToMany(mappedBy = "product")
    private List<PmsImage> productPmsImages;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
