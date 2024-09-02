package Midterm2.ex24;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

class Movie {
    static int maxRatingSize = 0;
    String title;
    List<Integer> ratings;

    public Movie(String title, int[] ratings) {
        this.title = title;
        this.ratings = IntStream.of(ratings)
                .boxed()
                .collect(Collectors.toList());
    }

    public int ratingsSize () {
        return ratings.size();
    }

    public double averageRating () {
        return ratings.stream().mapToInt(i ->i).sum() / (double) ratings.size();
    }
    public double ratingCoef () {
        return (averageRating() * ratingsSize()) / (double) maxRatingSize;
    }
    public String getTitle() {
        return title;
    }

    public static void setMaxRatingSize(int max) {
        if (max > maxRatingSize)
            Movie.maxRatingSize = max;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",
                title,
                averageRating(),
                ratingsSize());
    }
}

class MoviesList {

    Set<Movie> movieList;

    public MoviesList() {
        this.movieList = new TreeSet<>(Comparator.comparing(Movie::averageRating).reversed().thenComparing(Movie::getTitle));
    }

    public void addMovie(String title, int[] ratings) {
        movieList.add(new Movie(title, ratings));
        Movie.setMaxRatingSize(ratings.length);
    }

    public List<Movie> top10ByAvgRating() {
        return movieList.stream().limit(10).collect(Collectors.toList());
    }

    public List<Movie> top10ByRatingCoef() {
        return movieList.stream()
                .sorted(Comparator.comparing(Movie::ratingCoef)
                        .reversed().thenComparing(Movie::getTitle))
                .limit(10)
                .collect(Collectors.toList());
    }
}