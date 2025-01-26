package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.jpa.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    Region findById(int id);

    @Query("SELECT r FROM Region r WHERE r.id = :id")
    Optional<Region> findRegionById(@Param("id") Integer id);

    @Query("SELECT r FROM Region r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Region> findRegionsByName(@Param("name") String name);

    @Query("SELECT p FROM Pokemon p JOIN p.regions r WHERE r.id = :regionId")
    List<Pokemon> findPokemonByRegionId(@Param("regionId") Integer regionId);
}
