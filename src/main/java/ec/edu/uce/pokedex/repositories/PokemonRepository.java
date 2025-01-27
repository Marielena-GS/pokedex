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

    @Query("SELECT DISTINCT p FROM Pokemon p " +
            "JOIN p.types t " +
            "JOIN p.regions r " +
            "JOIN p.abilities a " +
            "JOIN p.habitat h " +
            "WHERE " +
            "(:type IS NULL OR t.name = :type) " +
            "AND (:region IS NULL OR r.name = :region) " +
            "AND (:ability IS NULL OR a.name = :ability) " +
            "AND (:habitat IS NULL OR h.name = :habitat)")
    List<Pokemon> findPokemonsByFilters(
            @Param("type") String type,
            @Param("region") String region,
            @Param("ability") String ability,
            @Param("habitat") String habitat);

    @Query("SELECT p FROM Pokemon p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Pokemon> findPokemonsByName(@Param("name") String name);


}
