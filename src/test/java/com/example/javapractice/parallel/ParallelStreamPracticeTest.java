package com.example.javapractice.parallel;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ParallelStreamPracticeTest {

    @Test
    void parallel_process_with_parallel_stream() {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();

        long start = System.nanoTime();
        // parallel stream
        int totalResult = targetNumbers.stream()
                .parallel()
                .mapToInt(number -> {
                    try {
                        Thread.sleep(10); // subTask 끼리 합할 때 10초 delay
                        return new SimpleNumberChunkCalculation(number).sum();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sum();

        // 결과 출력
        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("parallel2 = total : %s, timespent: %s msecs%n", totalResult, duration);
    }

    @Test
    void parallel_process_with_not_delayed_parallel_stream() {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();

        long start = System.nanoTime();
        // parallel stream
        int totalResult = targetNumbers.stream()
                .parallel()
                .mapToInt(subNumbers -> subNumbers.stream().reduce(0, Integer::sum))
                .sum();

        // 결과 출력
        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("parallel2 no delay = total : %s, timespent: %s msecs%n", totalResult, duration);
    }
}
