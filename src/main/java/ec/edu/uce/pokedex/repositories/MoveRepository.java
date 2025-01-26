package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Integer> {
    Move findById(int id);

    @Query("SELECT m FROM Move m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Move> findMovesByName(@Param("name") String name);
}
