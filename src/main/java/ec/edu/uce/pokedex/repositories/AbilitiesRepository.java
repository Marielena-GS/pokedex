package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Abilities;
import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbilitiesRepository extends JpaRepository<Abilities, Integer> {
    Abilities findById(int id);

    @Query("SELECT a FROM Abilities a WHERE a.id = :id")
    Optional<Abilities> findAbilityById(@Param("id") Integer id);

    @Query("SELECT a FROM Abilities a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Abilities> findAbilitiesByName(@Param("name") String name);

    @Query("SELECT p FROM Pokemon p JOIN p.abilities a WHERE a.id = :abilityId")
    List<Pokemon> findPokemonByAbilityId(@Param("abilityId") Integer abilityId);
}
