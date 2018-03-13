package ru.liga.fromuml;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.util.List;

abstract class Room {
    private double length;
    private double width;
    public double area() {
        throw new NotImplementedException();
    }
    public abstract BigDecimal repairPrice();
}

class Flat {
    private List<Room> rooms;
    private int floor;
}

class RepairInvoice {
    private List<Flat> flats;
    private String customer;
    public BigDecimal wholePrice() {
        throw new NotImplementedException();
    }
    public String report() {
        throw new NotImplementedException();
    }
}

class Bedroom extends Room {
    public BigDecimal repairPrice() {
        throw new NotImplementedException();
    }
}

class Kitchen extends Room {
    public BigDecimal repairPrice() {
        throw new NotImplementedException();
    }
}

class Bathroom extends Room {
    public BigDecimal repairPrice() {
        throw new NotImplementedException();
    }
}

public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
