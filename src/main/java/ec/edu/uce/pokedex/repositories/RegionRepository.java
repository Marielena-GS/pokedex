package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    Region findById(int id);
}
