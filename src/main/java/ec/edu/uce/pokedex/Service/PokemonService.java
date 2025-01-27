package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {
    @Autowired
    private PokemonRepository pokemonRepository;

    public List<Integer> getAllPokemonsPokemonIds() {
        return pokemonRepository.findAllPokemonIds();
    }

}
