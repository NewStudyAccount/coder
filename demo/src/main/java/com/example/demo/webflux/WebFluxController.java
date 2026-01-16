package com.example.demo.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webflux")
public class WebFluxController {

    @Autowired
    private WebFluxService webFluxService;

    @GetMapping("/mono")
    public Mono<String> getMono() {
        return Mono.just("Hello from Mono!");
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getStream() {
        return webFluxService.getStreamData();
    }

    // --- Creation Endpoints ---

    @GetMapping(value = "/flux/just", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxJust() {
        return webFluxService.fluxJust();
    }

    @GetMapping(value = "/flux/range", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxRange() {
        return webFluxService.fluxRange();
    }

    @GetMapping(value = "/flux/fromIterable", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxFromIterable() {
        return webFluxService.fluxFromIterable();
    }

    // --- Transformation Endpoints ---

    @GetMapping(value = "/flux/map", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxMap() {
        return webFluxService.fluxMap();
    }

    @GetMapping(value = "/flux/flatMap", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxFlatMap() {
        return webFluxService.fluxFlatMap();
    }

    // --- Filtering Endpoints ---

    @GetMapping(value = "/flux/filter", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxFilter() {
        return webFluxService.fluxFilter();
    }

    @GetMapping(value = "/flux/take", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxTake() {
        return webFluxService.fluxTake();
    }

    // --- Combination Endpoints ---

    @GetMapping(value = "/flux/zip", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxZip() {
        return webFluxService.fluxZip();
    }

    @GetMapping(value = "/flux/merge", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxMerge() {
        return webFluxService.fluxMerge();
    }

    // --- Error Handling Endpoints ---

    @GetMapping(value = "/flux/error", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getFluxError() {
        return webFluxService.fluxErrorHandling();
    }
}
