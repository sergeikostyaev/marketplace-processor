package bot.telegram.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import bot.telegram.dao.entity.Purchase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT e FROM Purchase e WHERE e.uid IN :uids")
    List<Purchase> findAllByUid(@Param("uids") List<String> uids);

    List<Purchase> findAllByDateBetween(LocalDateTime start, LocalDateTime end);

    default List<Purchase> findAllByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return findAllByDateBetween(startOfDay, endOfDay);
    }
}
