package com.bforbank.testing.web.rest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class RandomController {


    @GetMapping("/random")
    List<Integer> random(){
        List<Integer> arr = IntStream.range(0, 10)
            .boxed()
            .collect(Collectors.toList());
        Collections.shuffle(arr);
        return arr;
    }

}
