package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MenuService {
    private final Map<Long, Menu> menuMap = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    public List<Menu> listMenus() {
        return new ArrayList<>(menuMap.values());
    }

    public Menu getMenu(Long id) {
        return menuMap.get(id);
    }

    public Menu addMenu(Menu menu) {
        long id = idGen.incrementAndGet();
        menu.setId(id);
        menuMap.put(id, menu);
        return menu;
    }

    public Menu updateMenu(Long id, Menu menu) {
        if (!menuMap.containsKey(id)) return null;
        menu.setId(id);
        menuMap.put(id, menu);
        return menu;
    }

    public boolean deleteMenu(Long id) {
        return menuMap.remove(id) != null;
    }
}