package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Region;
import ec.edu.uce.pokedex.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<Region> findRegionByName(String name){
        return regionRepository.findRegionsByName(name);
    }

}
