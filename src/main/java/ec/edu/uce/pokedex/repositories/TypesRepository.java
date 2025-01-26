package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Types;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypesRepository extends JpaRepository<Types, Integer> {
    Types findByTypesId(int id);
}
