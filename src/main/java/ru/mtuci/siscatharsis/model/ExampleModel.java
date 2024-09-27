package ru.mtuci.siscatharsis.model;

public class ExampleModel {
    private String name;
    private int helloAmount;

    public ExampleModel() {
    }

    public ExampleModel(String name, int helloAmount) {
        this.name = name;
        this.helloAmount = helloAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String new_name) {
        this.name = new_name;
    }

    public int getHelloAmount() {
        return helloAmount;
    }

    public void setHelloAmount(int new_helloAmount) {
        this.helloAmount = new_helloAmount;
    }
}
