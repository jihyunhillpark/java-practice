package com.example.javapractice.parallel;

import java.util.List;
import java.util.concurrent.Callable;

public class SimpleNumberChunkCalculation implements Callable<Integer> {

    private final List<Integer> numbers;

    public SimpleNumberChunkCalculation(List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    public Integer call() throws Exception {
        return sum();
    }

    public Integer sum() throws InterruptedException {
        Integer sum = 0;
        for (Integer num : numbers) { // NOTE: Due to the experimentation with versions prior to Java8, steam api is not used.
            sum += num;
            Thread.sleep(10);
        }
        return sum;
    }
}
