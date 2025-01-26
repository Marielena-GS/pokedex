package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Habitat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitatRepository extends JpaRepository<Habitat, Integer> {
    Habitat findById(int id);

    @Query("SELECT h FROM Habitat h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Habitat> findHabitatByName(@Param("name") String name);

}
