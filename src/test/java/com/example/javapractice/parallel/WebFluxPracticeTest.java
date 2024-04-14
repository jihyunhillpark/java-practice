package com.example.javapractice.parallel;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
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

    @Test
    void parallel_process_with_webFlux() throws InterruptedException {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();
        CountDownLatch latch = new CountDownLatch(10); // 동시에 처리할 작업이 10개이므로
        AtomicInteger totalResult = new AtomicInteger(0);

        long start = System.nanoTime();

        Flux.fromIterable(targetNumbers)
                .parallel() // 병렬 처리 시작
                .runOn(Schedulers.parallel()) // 병렬 실행을 위한 스케줄러 설정
                .flatMap(chunkNumbers ->
                        // WebClient를 사용하여 REST API 요청 보내고 결과를 받아오는 Mono 생성 - 비동기 요청
                        WebClient.create("http://localhost:8080")
                                .post()
                                .uri("/v1/sum")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(chunkNumbers)
                                .retrieve()
                                .bodyToMono(Integer.class)
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
        System.out.printf("parallel4 with webFlux = total : %s, timespent: %s msecs%n", totalResult, duration);
    }

    @Test
    void parallel_process_with_blocking_client() throws InterruptedException {
        List<List<Integer>> targetNumbers = StubNumbers.getNumbers();
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger totalResult = new AtomicInteger(0);

        long start = System.nanoTime();

        RestTemplate restTemplate = new RestTemplate();

        Flux.fromIterable(targetNumbers)
                .parallel() // 병렬 처리 시작
                .runOn(Schedulers.parallel()) // 병렬 실행을 위한 스케줄러 설정
                .flatMap(chunkNumbers -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(chunkNumbers, headers);

                    ResponseEntity<Integer> responseEntity = restTemplate.postForEntity(
                            "http://localhost:8080/v1/sum",
                            requestEntity,
                            Integer.class
                    );

                    totalResult.addAndGet(responseEntity.getBody());
                    latch.countDown();
                    return Flux.empty(); // 빈 Flux 반환
                })
                .subscribe(); // 구독

        // 모든 작업의 결과를 기다림
        latch.await();

        long duration = (System.nanoTime() - start) / 1000000;
        System.out.printf("parallel4 with restTemplate = total : %s, timespent: %s msecs%n", totalResult, duration);
    }
}