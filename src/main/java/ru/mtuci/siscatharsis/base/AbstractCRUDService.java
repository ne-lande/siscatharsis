package ru.mtuci.siscatharsis.base;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public abstract class AbstractCRUDService<Model, Repository extends JpaRepository<Model, Long>> {

    private final Class<Model> modelClass;
    protected final Repository repository;

    protected AbstractCRUDService(Repository repository, Class<Model> modelClass) {
        this.repository = repository;
        this.modelClass = modelClass;
    }

    public Model findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                modelClass.getName() + " not found with the given ID"
        ));
    }

    public List<Model> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void save(Model entity) {
        repository.save(entity);
    }
}
