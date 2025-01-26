package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {

    private final PokemonRepository pokemonRepository;

    @Autowired
    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public Optional<Pokemon> findPokemonById(int id) {
        return pokemonRepository.findById(id);
    }
    public List<Pokemon> findPokemonByName(String name) {
        return pokemonRepository.findPokemonByName(name);
    }
    public List<Pokemon> findPokemonByAbilities(Integer abilitiesId) {
        return pokemonRepository.findPokemonByAbilityId(abilitiesId);
    }
    public List<Pokemon> findPokemonByHabitat(Integer habitatId) {
        return pokemonRepository.findPokemonByHabitatId(habitatId);
    }
    public List<Pokemon> findPokemonByMove(Integer moveId) {
        return pokemonRepository.findPokemonByMoveId(moveId);
    }
    public List<Pokemon> findPokemonByRegion(Integer regionId) {
        return pokemonRepository.findPokemonByRegionId(regionId);
    }
    public List<Pokemon> findPokemonByType(Integer typeId) {
        return pokemonRepository.findPokemonByTypeId(typeId);
    }
}
