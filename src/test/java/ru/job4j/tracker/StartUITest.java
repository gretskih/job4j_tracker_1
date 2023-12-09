package ru.job4j.tracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.job4j.tracker.action.*;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.input.StubInput;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.output.ConsoleOutput;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.output.StubOutput;
import ru.job4j.tracker.store.MemTracker;
import ru.job4j.tracker.store.Store;

import java.util.Arrays;
import java.util.Objects;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StartUITest {

    @Test
    public void findAllAction() {
        Output output = new StubOutput();
        MemTracker memTracker = new MemTracker();
        String is = System.lineSeparator();

        Item item1 = new Item();
        item1.setName("Item name1");

        Item item2 = new Item();
        item2.setName("Item name2");

        memTracker.add(item1);
        memTracker.add(item2);

        Input in = new StubInput(
                new String[] {"0", "1"}
        );
        UserAction[] actions = {
                new FindAllAction(output),
                new ExitAction(output)
        };
        new StartUI(output).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(output.toString(),
                "Menu." + is + "0. === Show all items ==="
                        + is + "1. Exit" + is + item1 + is + item2 + is
                        + "Menu." + is + "0. === Show all items ===" + is + "1. Exit" + is);
    }

    @Test
    public void whenInvalidExit() {
        Output out = new StubOutput();
        Input in = new StubInput(
                new String[] {"100", "0" }
        );
        MemTracker memTracker = new MemTracker();
        UserAction[] actions = {
                new ExitAction(out)
        };
        new StartUI(out).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(out.toString(), String.format(
                "Menu.%n"
                        + "0. Exit%n"
                        + "wrong input, you can select: 0.. 0%n"
                        + "Menu.%n"
                        + "0. Exit%n"
        ));
    }

    @Test
    public void whenCreateItem() {
        Output output = new ConsoleOutput();
        Input in = new StubInput(
                new String[] {"0", "Item name", "1"}
        );
        MemTracker memTracker = new MemTracker();
        UserAction[] actions = {
                new CreateAction(output),
                new ExitAction(output)
        };
        new StartUI(output).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(memTracker.findAll().get(0).getName(), "Item name");
    }

    @Test
    public void whenReplaceItem() {
        Output output = new ConsoleOutput();
        MemTracker memTracker = new MemTracker();
        Item item = memTracker.add(new Item("Replaced item"));
        String replacedName = "New item name";
        Input in = new StubInput(
                new String[] {"0", String.valueOf(item.getId()), "New item name", "1"}
        );
        UserAction[] actions = {
            new ReplaceAction(output),
            new ExitAction(output)
        };
        new StartUI(output).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(Objects.requireNonNull(memTracker.findById(item.getId())).getName(),
                replacedName);
    }

    @Test
    public void whenDeleteItem() {
        Output output = new ConsoleOutput();
        MemTracker memTracker = new MemTracker();
        Item item = memTracker.add(new Item("Deleted item"));
        Input in = new StubInput(
                new String[] {"0", String.valueOf(item.getId()), "1"}
        );
        UserAction[] actions = {
                new DeleteAction(output),
                new ExitAction(output)
        };
        new StartUI(output).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertNull(memTracker.findById(item.getId()));
    }

    @Test
    public void whenExit() {
        Output out = new StubOutput();
        Input in = new StubInput(
                new String[] {"0"}
        );
        MemTracker memTracker = new MemTracker();
        UserAction[] actions = {
                new ExitAction(out)
        };
        new StartUI(out).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(out.toString(),
                "Menu." + System.lineSeparator() + "0. Exit" + System.lineSeparator()
        );
    }

    @Test
    public void findByNameAction() {
        Output output = new StubOutput();
        MemTracker memTracker = new MemTracker();
        String is = System.lineSeparator();
        Item item = memTracker.add(new Item("ONEONE"));
        Input in = new StubInput(
                new String[] {"0", "ONEONE", "1"}
        );
        UserAction[] actions = {
                new FindByNameAction(output),
                new ExitAction(output)
        };
        new StartUI(output).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(output.toString(),
                "Menu." + is + "0. === Find items by name ==="
                        + is + "1. Exit" + is + item
                        + is + "Menu." + is + "0. === Find items by name ==="
                        + is + "1. Exit" + is);
    }

    @Test
    public void findByIdAction() {
        Output output = new StubOutput();
        MemTracker memTracker = new MemTracker();
        Item item = memTracker.add(new Item("Item name"));
        String is = System.lineSeparator();
        Input in = new StubInput(
                new String[] {"0", "1", "1"}
        );
        UserAction[] actions = {
                new FindByIdAction(output),
                new ExitAction(output)
        };
        new StartUI(output).init(in, memTracker, Arrays.asList(actions));
        Assertions.assertEquals(output.toString(), "Menu."
                + is + "0. === Find item by id ===" + is
                + "1. Exit" + is + "Founded item: " + item
                + is + "Menu." + is + "0. === Find item by id ===" + is + "1. Exit" + is);
    }

    @Test
    public void whenDeleteItemMock() {
        Output output = new StubOutput();
        Store store = new MemTracker();
        store.add(new Item("Deleted item"));
        DeleteAction del = new DeleteAction(output);
        String delId = "1";

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(delId);
        del.execute(input, store);

        String ln = System.lineSeparator();

        Assertions.assertEquals(output.toString(), "Item is successfully deleted!" + ln);
        Assertions.assertTrue(store.findAll().isEmpty());
    }

    @Test
    public void whenDeleteItemMockFail() {
        Output output = new StubOutput();
        Store store = new MemTracker();
        store.add(new Item("Deleted item"));
        DeleteAction del = new DeleteAction(output);
        String delId = "2";

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(delId);
        del.execute(input, store);

        String ln = System.lineSeparator();
        Assertions.assertEquals(output.toString(), "Wrong id!" + ln);
        Assertions.assertFalse(store.findAll().isEmpty());
    }

    @Test
    public void findByIdActionMock() {
        Output output = new StubOutput();
        Store store = new MemTracker();
        Item addItem = new Item("Found item");
        store.add(addItem);
        FindByIdAction find = new FindByIdAction(output);
        String findId = "1";

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(findId);
        find.execute(input, store);

        String ln = System.lineSeparator();
        Assertions.assertEquals(output.toString(), "Founded item: " + addItem + ln);
        Assertions.assertEquals(store.findAll().get(0).getName(), addItem.getName());
    }

    @Test
    public void findByIdActionMockFail() {
        Output output = new StubOutput();
        Store store = new MemTracker();
        Item addItem = new Item("Found item");
        store.add(addItem);
        FindByIdAction find = new FindByIdAction(output);
        String findId = "2";

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(findId);
        find.execute(input, store);

        String ln = System.lineSeparator();
        Assertions.assertEquals(output.toString(), "Wrong id! Not found" + ln);
        Assertions.assertEquals(store.findAll().get(0).getName(), addItem.getName());
    }

    @Test
    public void findByNameActionMock() {
        Output output = new StubOutput();
        Store store = new MemTracker();
        String findName = "Found item";
        Item addItem = new Item(findName);
        store.add(addItem);
        FindByNameAction find = new FindByNameAction(output);

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(findName);
        find.execute(input, store);

        String ln = System.lineSeparator();
        Assertions.assertEquals(output.toString(), addItem + ln);
        Assertions.assertEquals(store.findAll().get(0).getName(), findName);
    }

    @Test
    public void findByNameActionMockFail() {
        Output output = new StubOutput();
        Store store = new MemTracker();
        String findName = "Found item";
        Item addItem = new Item(findName);
        store.add(addItem);
        FindByNameAction find = new FindByNameAction(output);

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn("New item");
        find.execute(input, store);

        String ln = System.lineSeparator();
        Assertions.assertEquals(output.toString(), "Error, we can`t find item" + ln);
        Assertions.assertEquals(store.findAll().get(0).getName(), findName);
    }
}