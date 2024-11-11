package bot.telegram.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "good_ids")
public class GoodIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inner_id")
    private String innerId;

    @Column(name = "wb_id")
    private String wbId;

    @Column(name = "ozon_id")
    private String ozonId;

    @Column(name = "yandex_id")
    private String yandexId;

    @Column(name = "website_id")
    private String websiteId;

    @Column(name = "good_name")
    private String goodName;

    @Column(name = "yandex_link")
    private String yandexLink;



}
