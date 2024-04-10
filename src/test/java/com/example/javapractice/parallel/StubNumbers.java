package com.example.javapractice.parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StubNumbers {
    private static final List<List<Integer>> numbers = new ArrayList<>();

    static {
        for(int i = 0 ; i < 10 ; i++) {
            int start = i * 100;
            numbers.add(IntStream.rangeClosed(start + 1, start + 100)
                    .boxed()
                    .collect(Collectors.toList()));
        }
    }

    public static List<List<Integer>> getNumbers() {
        return Collections.unmodifiableList(numbers);
    }
}
