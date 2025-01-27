package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    @EntityGraph(attributePaths = {
            "envoles", // Obtener esta colección
            "types" // Obtener otra colección
    })
    Pokemon findById(int id);

    @Query("SELECT p FROM Pokemon p LEFT JOIN FETCH p.habitat LEFT JOIN FETCH p.regions WHERE p.id = :id")
    Optional<Pokemon> findByIdAndLoadHabitatAndRegions(@Param("id") int id);

    @Query("SELECT p.id FROM Pokemon p")
    List<Integer> findAllPokemonIds();
}
