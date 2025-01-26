package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    Pokemon findPokemonById(int id);

    @Query("SELECT p FROM Pokemon p WHERE p.id = :id")
    Optional<Pokemon> findPokemonById(@Param("id") Integer id);

    @Query("SELECT p FROM Pokemon p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Pokemon> findPokemonByName(@Param("name") String name);

    @Query("SELECT p FROM Pokemon p JOIN p.abilities a WHERE a.id = :abilitiesID")
    List<Pokemon> findPokemonByAbilityId(@Param("abilitiesID") Integer abilitiesID);

    @Query("SELECT p FROM Pokemon p JOIN p.habitat h WHERE h.id = :habitatId")
    List<Pokemon> findPokemonByHabitatId(@Param("habitatId") Integer habitatId);

    @Query("SELECT p FROM Pokemon p JOIN p.moves m WHERE m.id = :moveId")
    List<Pokemon> findPokemonByMoveId(@Param("moveId") Integer moveId);

    @Query("SELECT p FROM Pokemon p JOIN p.regions r WHERE r.id = :regionId")
    List<Pokemon> findPokemonByRegionId(@Param("regionId") Integer regionId);

    @Query("SELECT p FROM Pokemon p JOIN p.types t WHERE t.id = :typeId")
    List<Pokemon> findPokemonByTypeId(@Param("typeId") Integer typeId);
}
