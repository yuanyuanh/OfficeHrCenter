package com.example.officehrcenter.objects;

public class ProfOverviewDataModel {

    private int id;
    private String name;
    private String office;

    public ProfOverviewDataModel(int id, String name, String office){
        this.id = id;
        this.name = name;
        this.office = office;
    }

    public int getId(){return this.id;}

    public String getName(){return this.name;}

    public String getOffice(){return this.office;}

}
