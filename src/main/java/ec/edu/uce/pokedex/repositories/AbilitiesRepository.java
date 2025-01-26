package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Abilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbilitiesRepository extends JpaRepository<Abilities, Integer> {
    Abilities findByAbilitiesId(int id);
}
