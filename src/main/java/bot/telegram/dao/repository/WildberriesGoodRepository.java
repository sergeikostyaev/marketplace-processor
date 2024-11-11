package bot.telegram.dao.repository;

import bot.telegram.dao.entity.WildberriesGood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WildberriesGoodRepository extends JpaRepository<WildberriesGood, Long> {

    WildberriesGood findByWbId(String wbId);

}
