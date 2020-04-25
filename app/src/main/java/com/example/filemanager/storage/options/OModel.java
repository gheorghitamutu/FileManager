package com.example.filemanager.storage.options;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OModel {

    static final List<Item> items = new ArrayList<>();
    private static final List<String> options = new ArrayList<>(
            Arrays.asList("Rename", "Copy", "Move", "Delete"));

    static {
        for (int i = 0; i < options.size(); i++) {
            addItem(createOption(options.get(i)));
        }
    }

    private static void addItem(Item item) {
        items.add(item);
    }

    private static Item createOption(String name) {
        return new Item(name);
    }

    public static class Item {
        final String option;

        Item(String content) {
            this.option = content;
        }

        public String getOption() {
            return option;
        }

        @NonNull
        @Override
        public String toString() {
            return option;
        }
    }
}
