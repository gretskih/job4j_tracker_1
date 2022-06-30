package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.store.MemTracker;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.store.Store;

public class FindByIdAction implements UserAction {
    private final Output out;

    public FindByIdAction(Output out) {
        this.out = out;
    }

    @Override
    public String name() {
        return "=== Find item by id ===";
    }

    @Override
    public boolean execute(Input input, Store store) {
        int id = Integer.parseInt(input.askStr("enter Id"));
        Item item = store.findById(id);

        if (item != null) {
            out.println("Founded item: " + item.getName());
        } else {
            out.println("Wrong id! Not found");
        }
        return true;
    }
}
