package ru.job4j.tracker;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.tracker.model.Item;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateRun {
    public static void main(String[] args) {
        var registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try (var sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory()) {
            var session = sf
                    .withOptions()
                    .openSession();
            session.beginTransaction();
            var item = new Item();
            item.setName("check timezone");
            item.setCreated(LocalDateTime.now());
            session.persist(item);

            var stored = session.createQuery(
                    "from Item", Item.class
            ).list();

            session.getTransaction().commit();
            session.close();

            for (Item it : stored) {
                var time = it.getCreated().atZone(
                        ZoneId.of("UTC+3")
                ).withZoneSameInstant(ZoneId.of("UTC+5"))
                        .format(DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd"));
                System.out.println("По времени Урала: " + time);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
