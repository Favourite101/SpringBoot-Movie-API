package com.movieflix.movieapi.repositories;

import com.movieflix.movieapi.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository  extends JpaRepository<Movie, Integer> {
}
