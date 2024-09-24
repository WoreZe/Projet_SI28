package fr.utc.multeract.server.repositories;

import fr.utc.multeract.server.models.Story;
import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.models.HistoryInstanceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface HistoryInstanceRepository extends JpaRepository<StoryInstance, Long> {
    List<StoryInstance> findAllByHistory(Story history);

    List<StoryInstance> findAllByHistoryId(Long id);

    Optional<StoryInstance> findByJoinCode(String joinCode);

    List<StoryInstance> findAllByStateIs(HistoryInstanceState state);

    //by state and history
    List<StoryInstance> findAllByStateIsAndHistory(HistoryInstanceState state, Story history);
}