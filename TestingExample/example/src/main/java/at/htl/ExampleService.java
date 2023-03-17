package at.htl;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class ExampleService {
    private final List<Item> items = List.of(new Item(1L, "Hello"));

    public List<Item> getAllItems() {
        return items;
    }

    public Optional<Item> getItem(Long id) {
        return items.stream()
                .filter(item -> Objects.equals(id, item.getId()))
                .findFirst();
    }
}
