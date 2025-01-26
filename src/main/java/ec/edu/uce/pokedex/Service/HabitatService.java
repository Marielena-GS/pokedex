package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.repositories.HabitatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitatService {

    private final HabitatRepository habitatRepository;

    @Autowired
    public HabitatService(HabitatRepository habitatRepository) {
        this.habitatRepository = habitatRepository;
    }
    public List<Habitat> findHabitatsByName(String name) {
        return habitatRepository.findHabitatByName(name);
    }
}
