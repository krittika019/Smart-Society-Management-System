package com.ssms.smartsocietymanagement.model;

public class Flat {
    private String flat_id;
    private String block_name;
    private String flat_number;

    public Flat(String flat_id, String block_name, String flat_number) {
        this.flat_id = flat_id;
        this.block_name = block_name;
        this.flat_number = flat_number;
    }

    public String getFlat_id() {
        return flat_id;
    }

    public void setFlat_id(String flat_id) {
        this.flat_id = flat_id;
    }

    public String getBlock_name() {
        return block_name;
    }

    public void setBlock_name(String block_name) {
        this.block_name = block_name;
    }

    public String getFlat_number() {
        return flat_number;
    }

    public void setFlat_number(String flat_number) {
        this.flat_number = flat_number;
    }
}
