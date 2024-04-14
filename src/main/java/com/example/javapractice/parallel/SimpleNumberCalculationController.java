package com.example.javapractice.parallel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class SimpleNumberCalculationController {

    @PostMapping("/v1/sum")
    public Integer sum(@RequestBody List<Integer> numbers) {
        return new SimpleNumberChunkCalculation(numbers).sum();
    }

    @GetMapping("/v1/Hello")
    public void hello(@RequestBody List<Integer> numbers) {
        System.out.println(numbers);
    }
}
