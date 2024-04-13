package com.example.javapractice.parallel;

import java.util.List;
import java.util.concurrent.Callable;

public class SimpleNumberChunkCalculation implements Callable<Integer> {

    private final List<Integer> numbers;

    public SimpleNumberChunkCalculation(List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    public Integer call() {
        return sum();
    }

    public Integer sum() {
        Integer sum = 0;
        for (Integer num : numbers) { // NOTE: Due to the experimentation with versions prior to Java8, steam api is not used.
            sum += num;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return sum;
    }
}
