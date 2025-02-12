package com.movieflix.movieapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's title!")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's genre!")
    private String genre;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's director!")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's studio!")
    private String studio;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's poster!")
    private String poster;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

}
