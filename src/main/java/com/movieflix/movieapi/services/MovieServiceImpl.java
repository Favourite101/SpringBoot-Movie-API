package com.movieflix.movieapi.services;

import com.movieflix.movieapi.dto.MovieDto;
import com.movieflix.movieapi.dto.MoviePageResponse;
import com.movieflix.movieapi.entities.Movie;
import com.movieflix.movieapi.exceptions.FileExistsException;
import com.movieflix.movieapi.exceptions.MovieNotFoundException;
import com.movieflix.movieapi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }


    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        //upload file
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please enter another file name!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        //set the value of field "poster" as filename
        movieDto.setPoster(uploadedFileName);

        //map dto to movie object
        Movie movie = new Movie(
                null,
                movieDto.getReleaseYear(),
                movieDto.getTitle(),
                movieDto.getGenre(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getPoster(),
                movieDto.getMovieCast()
        );

        //save the movie -> saved movie object
        Movie savedMovie = movieRepository.save(movie);

        //generate the posterUrl
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        //map movie object to dto object and return it
        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getReleaseYear(),
                savedMovie.getTitle(),
                savedMovie.getGenre(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getPoster(),
                posterUrl,
                savedMovie.getMovieCast()
        );
    }

    @Override
    public MovieDto getMovie(Integer movieId) {

        // check the data in db and if exists, fetch the data of given id
        Movie movie = movieRepository.findById(movieId).orElseThrow( () -> new MovieNotFoundException("Movie not found with ID = " + movieId));

        //generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        //map to movie dto object and return it
        return new MovieDto(
                movie.getMovieId(),
                movie.getReleaseYear(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getPoster(),
                posterUrl,
                movie.getMovieCast()
        );
    }

    @Override
    public List<MovieDto> getAllMovies() {

        // fetch all data from db
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        //iterate through the list, generate posterUrl for each movie object
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getReleaseYear(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getPoster(),
                    posterUrl,
                    movie.getMovieCast()
            );
            movieDtos.add(response);
        }
        //and map to movieDto object
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {

        //check if movie exists
        Movie movie = movieRepository.findById(movieId).orElseThrow( () -> new MovieNotFoundException("Movie not found with ID = " + movieId));

        //if file is null, do nothing else replace existing file with new one
        String fileName = movie.getPoster();

        if (file != null) {
            Files.delete(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        //set the value of field "poster" depending on the step above
        movieDto.setPoster(fileName);

        //map movieDto to movie object
        Movie updatedMovie = new Movie(
                movieId,
                movieDto.getReleaseYear(),
                movieDto.getTitle(),
                movieDto.getGenre(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getPoster(),
                movieDto.getMovieCast()
        );

        //update movie
        movieRepository.save(updatedMovie);

        //generate posterUrl
        String posterUrl = baseUrl + "/file/" + fileName;

        //map movie object to dto object and return it
        return new MovieDto(
                updatedMovie.getMovieId(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getTitle(),
                updatedMovie.getGenre(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getPoster(),
                posterUrl,
                updatedMovie.getMovieCast()
        );
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {

        //check if movie object exists
        Movie movie = movieRepository.findById(movieId).orElseThrow( () -> new MovieNotFoundException("Movie not found with ID = " + movieId));

        //delete associated file
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        //delete movie
        movieRepository.delete(movie);

        //return success message
        String movieName = movie.getTitle();
        Integer id = movie.getMovieId();
        return movieName + " with ID = " + id + " has been successfully deleted!";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages= movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        //iterate through the list, generate posterUrl for each movie object
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getReleaseYear(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getPoster(),
                    posterUrl,
                    movie.getMovieCast()
            );
            movieDtos.add(response);
        }
        return new MoviePageResponse(movieDtos, pageNumber, pageSize, (int) moviePages.getTotalElements(),
                moviePages.getTotalPages(), moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviePages= movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        //iterate through the list, generate posterUrl for each movie object
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto response = new MovieDto(
                    movie.getMovieId(),
                    movie.getReleaseYear(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getPoster(),
                    posterUrl,
                    movie.getMovieCast()
            );
            movieDtos.add(response);
        }
        return new MoviePageResponse(movieDtos, pageNumber, pageSize, (int) moviePages.getTotalElements(),
                moviePages.getTotalPages(), moviePages.isLast());
    }

}
