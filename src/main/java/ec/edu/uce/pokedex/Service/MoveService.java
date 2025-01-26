package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Move;
import ec.edu.uce.pokedex.repositories.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {

    private final MoveRepository moveRepository;

    @Autowired
    public MoveService(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    public List<Move> findMoveByName(String name) {
        return moveRepository.findMovesByName(name);
    }
}
