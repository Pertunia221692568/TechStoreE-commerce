package za.ac.cput.controller;

import za.ac.cput.domain.ShoppingCart;
import za.ac.cput.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService service;

    @PostMapping("/create")
    public ShoppingCart create(@RequestBody ShoppingCart cart) {
        return service.save(cart);
    }

    @GetMapping("/getall")
    public List<ShoppingCart> getAll() {
        return service.findAll();
    }

    @PostMapping("/update")
    public ShoppingCart update(@RequestBody ShoppingCart cart) {
        return service.update(cart);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody ShoppingCart cart) {
        return service.delete(cart);
    }
}
