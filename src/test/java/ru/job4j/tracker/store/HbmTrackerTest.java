package ru.job4j.tracker.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import ru.job4j.tracker.model.Item;

import java.util.List;

public class HbmTrackerTest {

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    /**
     * Очистка базы
     */
    @BeforeEach
    public void clearTableItem() {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.createQuery("DELETE FROM Item").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Поиск по Id
     */
    @Test
    public void whenAddNewItemThenGetItemById() {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            item.setName("test1");
            tracker.add(item);
            Item result = tracker.findById(item.getId());
            assertThat(result.getName()).isEqualTo(item.getName());
        }
    }

    /**
     * Поиск по name
     */
    @Test
    public void whenAddNewItemThenGetItemByName() {
        try (var tracker = new HbmTracker()) {
            Item item1 = new Item();
            item1.setName("test");
            Item item2 = new Item();
            item2.setName("test");
            tracker.add(item1);
            tracker.add(item2);
            List<Item> result = tracker.findByName(item1.getName());
            assertThat(result).isEqualTo(List.of(item1, item2));
        }
    }

    /**
     * Извлечение списка всех записей
     */
    @Test
    public void whenAddNewItemThenGetAllItems() {
        try (var tracker = new HbmTracker()) {
            Item item1 = new Item();
            item1.setName("test1");
            Item item2 = new Item();
            item2.setName("test2");
            tracker.add(item1);
            tracker.add(item2);
            List<Item> result = tracker.findAll();
            assertThat(result).isEqualTo(List.of(item1, item2));
        }
    }

    /**
     * Перезаписывание записи
     */
    @Test
    public void whenReplaceItemThenGetNewItem() {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            item.setName("test1");
            tracker.add(item);

            item.setName("test2");
            tracker.replace(item.getId(), item);

            Item resultItem = tracker.findById(item.getId());
            assertThat(resultItem).isEqualTo(item);
        }
    }

    /**
     * Удаление записи
     */
    @Test
    public void whenAddTwoItemsAndDeleteOneItemThenGetOneItem() {
        try (var tracker = new HbmTracker()) {
            Item item1 = new Item();
            item1.setName("test1");
            Item item2 = new Item();
            item2.setName("test2");
            tracker.add(item1);
            tracker.add(item2);

            tracker.delete(item1.getId());

            List<Item> result = tracker.findAll();
            assertThat(result).isEqualTo(List.of(item2));
        }
    }
}
