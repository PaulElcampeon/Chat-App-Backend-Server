package com.P.G.chatappbackend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "NAMES")
public class NameHolder {

    @Id
    private String name;

    public NameHolder() {}

    public NameHolder(String name) {
        this.name = name;
    }

}

