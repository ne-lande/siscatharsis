package ru.mtuci.siscatharsis.base;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.utils.ApiMessage;
import java.util.List;

public abstract class AbstractCRUDController<Model, DTO, Repository extends JpaRepository<Model, Long>, Service extends AbstractCRUDService<Model, DTO, Repository>> {

    protected final Service service;

    protected AbstractCRUDController(Service service) {
        this.service = service;
    }

    protected List<Model> getAllEntities() {
        return service.findAll();
    }

    protected Model getEntityById(Long id) {
        return service.findById(id);
    }

    protected Model createEntity(DTO entity) {
        return service.create(entity);
    }

    protected Model updateEntity(Long id, DTO entity) {
        return service.update(id, entity);
    }

    protected void deleteEntity(Long id) {
        service.deleteById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DTO requestDTO) {
        Model createdEntity = createEntity(requestDTO);
        return ApiMessage.Success(createdEntity);
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll() {
        return ApiMessage.Success(getAllEntities());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Model entity = getEntityById(id);
        if (entity == null) {
            return ApiMessage.BadRequest("Entity not found");
        }
        return ApiMessage.Success(entity);
    }

    @PutMapping("/update{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DTO requestDTO) {
        Model updatedEntity = updateEntity(id, requestDTO);
        if (updatedEntity == null) {
            return ApiMessage.BadRequest("Entity not found");
        }
        return ApiMessage.Success(updatedEntity);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        deleteEntity(id);
        return ApiMessage.Success("Deleted successfully");
    }
}
