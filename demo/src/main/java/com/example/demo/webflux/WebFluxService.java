package com.example.demo.webflux;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class WebFluxService {

    /**
     * Creates a Flux that emits a sequence of integers every second.
     * This simulates a stream of data coming from a service.
     */
    public Flux<String> getStreamData() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Flux Event - " + sequence + " at " + java.time.LocalTime.now());
    }

    // --- Creation Operators ---

    public Flux<String> fluxJust() {
        return Flux.just("Apple", "Banana", "Cherry")
                .map(fruit -> "Just: " + fruit);
    }

    public Flux<String> fluxRange() {
        return Flux.range(1, 5)
                .map(i -> "Range: " + i);
    }

    public Flux<String> fluxFromIterable() {
        List<String> list = Arrays.asList("Red", "Green", "Blue");
        return Flux.fromIterable(list)
                .map(color -> "Iterable: " + color);
    }

    // --- Transformation Operators ---

    public Flux<String> fluxMap() {
        return Flux.range(1, 5)
                .map(i -> "Map: " + i + " * 2 = " + (i * 2));
    }

    public Flux<String> fluxFlatMap() {
        return Flux.range(1, 5)
                .flatMap(i -> Flux.just(i)
                        .delayElements(Duration.ofMillis(new Random().nextInt(500))) // Simulate variable delay
                        .map(n -> "FlatMap (Async): " + n + " processed at " + java.time.LocalTime.now()));
    }

    // --- Filtering Operators ---

    public Flux<String> fluxFilter() {
        return Flux.range(1, 10)
                .filter(i -> i % 2 == 0)
                .map(i -> "Filter (Even): " + i);
    }

    public Flux<String> fluxTake() {
        return Flux.interval(Duration.ofMillis(100))
                .take(5)
                .map(i -> "Take (Limit 5): " + i);
    }

    // --- Combination Operators ---

    public Flux<String> fluxZip() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("1", "2", "3");
        return Flux.zip(flux1, flux2, (s1, s2) -> "Zip: " + s1 + " + " + s2);
    }

    public Flux<String> fluxMerge() {
        Flux<String> flux1 = Flux.interval(Duration.ofMillis(200)).take(3).map(i -> "Merge 1: " + i);
        Flux<String> flux2 = Flux.interval(Duration.ofMillis(300)).take(3).map(i -> "Merge 2: " + i);
        return Flux.merge(flux1, flux2);
    }

    // --- Error Handling Operators ---

    public Flux<String> fluxErrorHandling() {
        return Flux.range(1, 5)
                .map(i -> {
                    if (i == 4) throw new RuntimeException("Simulated Error");
                    return "Value: " + i;
                })
                .onErrorReturn("Error handled: Default Value");
    }
}
