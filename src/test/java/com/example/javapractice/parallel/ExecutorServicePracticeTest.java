package com.example.javapractice.parallel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServicePracticeTest {

    @Test
    void sequential_process_when_simple_sequential_iteration() throws InterruptedException {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();

        long start = System.nanoTime();
        Integer totalResult = 0;
        for(List<Integer> numbers : targetNumbers) {
            for(Integer number : numbers) {
                totalResult += number;
                Thread.sleep(10);
            }
        }

        long duration = (System.nanoTime() - start)/1000000;
        System.out.printf("sequential iteration = total : %s, timespent: %s msecs%n", totalResult, duration);
    }

    @Test
    void parallel_process_when_ExecutorService_is_fixedSizePool() throws InterruptedException, ExecutionException {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<Integer>> tasks = new ArrayList<>();

        long start = System.nanoTime();
        int totalResult = 0;
        for (List<Integer> numbers : targetNumbers) {
            tasks.add(new SimpleNumberChunkCalculation(numbers));
        }
        List<Future<Integer>> results = executor.invokeAll(tasks);
        executor.shutdownNow();

        for (Future<Integer> result : results) {
            totalResult += result.get();
            Thread.sleep(10);
        }

        long duration = (System.nanoTime() - start)/1000000;
        System.out.printf("parallel1 = total : %s, timespent: %s msecs%n", totalResult, duration);
    }
}
