package fr.utc.multeract.server.repositories;

import fr.utc.multeract.server.models.StoryStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryStepRepository extends JpaRepository<StoryStep, Long> {
    List<StoryStep> findAllByHistoryId(Long aLong);
}
