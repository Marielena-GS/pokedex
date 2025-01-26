package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitatRepository extends JpaRepository<Habitat, Integer> {
    Habitat findById(int id);

    @Query("SELECT h FROM Habitat h WHERE h.id = :id")
    Optional<Habitat> findHabitatById(@Param("id") Integer id);

    @Query("SELECT h FROM Habitat h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Habitat> findHabitatsByName(@Param("name") String name);

    @Query("SELECT p FROM Pokemon p JOIN p.habitat h WHERE h.id = :habitatId")
    List<Pokemon> findPokemonByHabitatId(@Param("habitatId") Integer habitatId);
}
