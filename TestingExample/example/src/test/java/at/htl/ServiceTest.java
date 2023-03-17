package at.htl;

import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    ExampleService service = new ExampleService();

    @Test
    public void getAllItems() {
        var items = service.getAllItems();
        assertTrue(items.size() == 1);

        var item = items.get(0);
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Hello", item.getMessage());
    }

    @Test
    public void getItemFound() {
        var item = service.getItem(1L);
        assertNotNull(item);
        assertTrue(item.isPresent());
        assertEquals(1L, item.get().getId());
        assertEquals("Hello", item.get().getMessage());
    }

    @Test
    public void getItemNotFound() {
        var item = service.getItem(2L);
        assertNotNull(item);
        assertTrue(item.isEmpty());
    }
}
