package br.com.diegoczajka.quarkussocial.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(name = "post_text")
    private String text;
    @Column(name = "dateTime")
    private LocalDateTime dateTime;
    @ManyToOne
    @JoinColumn
    private User user;

    @PrePersist
    public void prePersist() {
        setDateTime(LocalDateTime.now());
    }

}
