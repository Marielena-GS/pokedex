package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Habitat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitatRepository extends JpaRepository<Habitat, Integer> {
    Habitat findByHabitatId(int id);
}
