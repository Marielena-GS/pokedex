package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Move;
import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoveRepository extends JpaRepository<Move, Integer> {
    Move findById(int id);

    @Query("SELECT m FROM Move m WHERE m.id = :id")
    Optional<Move> findMoveById(@Param("id") Integer id);

    @Query("SELECT m FROM Move m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Move> findMovesByName(@Param("name") String name);

    @Query("SELECT p FROM Pokemon p JOIN p.moves m WHERE m.id = :moveId")
    List<Pokemon> findPokemonByMoveId(@Param("moveId") Integer moveId);

}
