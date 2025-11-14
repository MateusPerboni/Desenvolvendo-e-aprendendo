package com.example.registrocarros.model;

import com.google.gson.annotations.SerializedName;

public class Carro {
    @SerializedName("id")
    private int id;

    @SerializedName("marca")
    private String marca;

    @SerializedName("modelo")
    private String modelo;

    @SerializedName("ano")
    private int ano;

    @SerializedName("preco")
    private double preco;

    @SerializedName("descricao")
    private String descricao;

    // Construtor vazio necessário para o Gson
    public Carro() {}

    // Construtor completo
    public Carro(String marca, String modelo, int ano, double preco, String descricao) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.preco = preco;
        this.descricao = descricao;
    }

    // Getters
    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public double getPreco() { return preco; }
    public String getDescricao() { return descricao; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setAno(int ano) { this.ano = ano; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "Carro{" +
                "id=" + id +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", ano=" + ano +
                ", preco=" + preco +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
