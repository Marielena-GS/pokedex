package ec.edu.uce.pokedex.repositories;

import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    Pokemon findPokemonById(int id);
}
