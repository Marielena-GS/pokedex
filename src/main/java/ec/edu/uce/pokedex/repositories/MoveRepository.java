package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveRepository extends JpaRepository<Move, Integer> {

    Move findById(int id);

}
