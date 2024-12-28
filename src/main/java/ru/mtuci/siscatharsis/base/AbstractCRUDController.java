package ru.mtuci.siscatharsis.base;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.utils.ApiMessage;

/*
Такую абстракцию лучше использовать только для моделей и сервисов у которых есть необходимость в реализации методов CRUD
*/
public abstract class AbstractCRUDController<
    Model,
    Repository extends JpaRepository<Model, Long>,
    Service extends AbstractCRUDService<Model, Repository>>
    {

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

    protected void deleteEntity(Long id) {
        service.deleteById(id);
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll() {
        return ApiMessage.Success(getAllEntities());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ApiMessage.Success(getEntityById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        deleteEntity(id);
        return ApiMessage.Success("Deleted successfully");
    }
}
