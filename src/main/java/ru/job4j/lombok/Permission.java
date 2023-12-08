package ru.job4j.lombok;

import lombok.*;

import java.util.List;

@Builder(builderMethodName = "of")
@ToString
public class Permission {
    private int id;
    private String name;

    @Singular("accessBy")
    private List<String> rules;

    public static void main(String[] args) {
        Permission build = Permission.of()
                .name("One")
                .id(1)
                .accessBy("One")
                .accessBy("Two")
                .build();
        System.out.println(build);
    }
}
