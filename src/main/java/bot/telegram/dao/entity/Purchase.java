package bot.telegram.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import bot.telegram.common.MarketplaceCode;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inner_id")
    private String innerId;

    @Column(name = "wildberries_id")
    private String wildberriesId;

    @Column(name = "yandex_id")
    private String yandexId;

    @Column(name = "ozon_id")
    private String ozonId;

    @Column(name = "website_id")
    private String websiteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "marketplace_code")
    private MarketplaceCode marketplaceCode;

    @Column(name = "uid")
    private String uid;

    @Setter
    @Column(name = "status")
    private String status;

    @Column(name = "purchase_name")
    private String name;

    @Column(name = "purchase_date")
    private LocalDateTime date;

    @Column(name = "region")
    private String region;

    @Column(name = "price")
    private String price;

    @Column(name = "yandex_link")
    private String yandexLink;
}
