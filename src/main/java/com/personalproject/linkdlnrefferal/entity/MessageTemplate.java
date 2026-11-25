package com.personalproject.linkdlnrefferal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message_templates", indexes = @Index(name = "idx_message_templates_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class MessageTemplate extends OwnedEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> variables = new ArrayList<>();

    @Column(name = "is_favorite", nullable = false)
    private boolean favorite;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();
}
