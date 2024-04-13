package com.example.javapractice.parallel;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class CompletableFuturePracticeTest {

    private final Executor executor = Executors.newFixedThreadPool(10, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    @Test
    void parallel_process_with_CompletableFuture() {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();

        long start = System.nanoTime();
        List<CompletableFuture<Integer>> chunkResult = targetNumbers.stream()
                .map(numbers -> CompletableFuture.supplyAsync( // 비동기로 처리
                        () -> new SimpleNumberChunkCalculation(numbers).sum(),
                        executor //  executor를 넘겨주지 않으면 ForkJoinPool.getCommonPoolParallelism()의 수만큼 쓰레드 사용
                ))
                .toList();

        Integer totalResult = chunkResult.stream()
                .map(CompletableFuture::join) // 모든 비동기 작업이 끝나길 기다려 합산
                .reduce(0, Integer::sum);

        // 결과 출력
        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("number of Threads : %s\n", Runtime.getRuntime().availableProcessors());
        System.out.printf("number of genuinely used Threads : %s\n", ForkJoinPool.getCommonPoolParallelism()); // 디폴트 쓰레드의 개수
        System.out.printf("parallel3  = total : %s, timespent: %s msecs%n", totalResult, duration);
    }

}
