package fr.utc.multeract.server.repositories;

import fr.utc.multeract.server.models.HistoryVisibility;
import fr.utc.multeract.server.models.Story;
import fr.utc.multeract.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoryRepository extends JpaRepository<Story, Long> {
    List<Story> findAllByCreator(User creator);

    List<Story> findAllByVisibilityIs(HistoryVisibility visibility);
}