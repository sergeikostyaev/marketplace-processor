package bot.telegram.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import bot.telegram.dao.entity.GoodIdentifier;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodIdRepository extends JpaRepository<GoodIdentifier, Long> {

    GoodIdentifier findByOzonId(String ozonId);

    GoodIdentifier findByWbId(String wbId);

    GoodIdentifier findByYandexLink(String yandexId);

    GoodIdentifier findByInnerId(String innerId);

}
