package com.example.javapractice.parallel;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class WebFluxPracticeTest {

    @Test
    void parallel_process_with_reactor() throws InterruptedException {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();
        CountDownLatch latch = new CountDownLatch(10); // 동시에 처리할 작업이 10개이므로
        AtomicInteger totalResult = new AtomicInteger(0);

        long start = System.nanoTime();

        Flux.fromIterable(targetNumbers)
                .parallel() // 병렬 처리 시작
                .runOn(Schedulers.parallel()) // 병렬 실행을 위한 스케줄러 설정
                .flatMap(chunkNumbers ->
                        Mono.fromCallable(() -> new SimpleNumberChunkCalculation(chunkNumbers).sum())
                )
                .subscribe(
                        totalResult::addAndGet,
                        error -> {
                            System.err.println("Error occurred: " + error);
                            latch.countDown();
                        },
                        latch::countDown
                );

        // 모든 작업의 결과를 기다림
        latch.await();

        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("parallel4  = total : %s, timespent: %s msecs%n", totalResult, duration);
    }
}