package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.jpa.Types;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypesRepository extends JpaRepository<Types, Integer> {
    Types findById(int id);

    @Query("SELECT t FROM Types t WHERE t.id = :id")
    Optional<Types> findTypeById(@Param("id") Integer id);

    @Query("SELECT t FROM Types t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Types> findTypesByName(@Param("name") String name);

    @Query("SELECT p FROM Pokemon p JOIN p.types t WHERE t.id = :typeId")
    List<Pokemon> findPokemonByTypeId(@Param("typeId") Integer typeId);
}

