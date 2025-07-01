package br.com.microservices.orchestrated.orderservice.controllers;

import br.com.microservices.orchestrated.orderservice.documents.Event;
import br.com.microservices.orchestrated.orderservice.documents.Order;
import br.com.microservices.orchestrated.orderservice.dtos.EventFilters;
import br.com.microservices.orchestrated.orderservice.dtos.OrderRequest;
import br.com.microservices.orchestrated.orderservice.services.EventService;
import br.com.microservices.orchestrated.orderservice.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Event findEvent(@RequestBody EventFilters filter) {
        return eventService.findEvent(filter);
    }

    @GetMapping("/all")
    public List<Event> findAll() {
        return eventService.findAll();
    }
}
