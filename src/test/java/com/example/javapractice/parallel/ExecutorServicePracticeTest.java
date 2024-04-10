package com.example.javapractice.parallel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServicePracticeTest {

    @Test
    void sequential_process_when_simple_sequential_iteration() throws InterruptedException {
        long start = System.nanoTime();
        int totalResult = 0;
        for (int number = 0; number <= 1000; number++) {
            totalResult += number;
            Thread.sleep(10);
        }

        // 결과 출력
        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("sequential iteration = total : %s, timespent: %s msecs%n", totalResult, duration);
    }

    @Test
    void parallel_process_when_ExecutorService_is_fixedSizePool() throws InterruptedException, ExecutionException {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        long start = System.nanoTime();
        int totalResult = 0;
        // parallel : multi-threading
        List<Future<Integer>> results = new ArrayList<>();
        for (List<Integer> numbers : targetNumbers) {
            results.add(executor.submit(new SimpleNumberChunkCalculation(numbers)));
        }

        for (Future<Integer> result : results) {
            totalResult += result.get(); // 비동기 작업의 결과를 가져오는 부분
            Thread.sleep(10);
        }
        executor.shutdownNow(); // 이미 제출된 작업들을 인터럽트&종료

        // 결과 출력
        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("parallel1 = total : %s, timespent: %s msecs%n", totalResult, duration);
    }
}
