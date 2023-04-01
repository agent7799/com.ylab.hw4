package io.lesson04.movie;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

public class MovieLoaderImpl implements MovieLoader {
    private DataSource dataSource;

    public MovieLoaderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void loadData(File file) throws IOException, SQLException {
        String putMovieIntoDB = "insert into movie (year, length, title, subject, actors, actress, director, popularity, awards) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            br.readLine();
            while (br.ready()) {
                String[] filmFields = br.readLine().split(";");
                Movie movie = new Movie();
                movie.setYear(filmFields[0].isBlank() ? null : Integer.parseInt(filmFields[0]));
                movie.setLength(filmFields[1].isBlank() ? null : Integer.parseInt(filmFields[1]));
                movie.setTitle(filmFields[2].isBlank() ? null : filmFields[2]);
                movie.setSubject(filmFields[3].isBlank() ? null : filmFields[3]);
                movie.setActors(filmFields[4].isBlank() ? null : filmFields[4]);
                movie.setActress(filmFields[5].isBlank() ? null : filmFields[5]);
                movie.setDirector(filmFields[6].isBlank() ? null : filmFields[6]);
                movie.setPopularity(filmFields[7].isBlank() ? null : Integer.parseInt(filmFields[7]));
                movie.setAwards(filmFields[8].isBlank() ? null : filmFields[8].equals("Yes"));

                try (Connection connection = dataSource.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(putMovieIntoDB)) {
                    if (movie.getYear() == null) {
                        preparedStatement.setNull(1, Types.INTEGER);
                    } else {
                        preparedStatement.setInt(1, movie.getYear());
                    }
                    if (movie.getLength() == null) {
                        preparedStatement.setNull(2, Types.INTEGER);
                    } else {
                        preparedStatement.setInt(2, movie.getLength());
                    }
                    if (movie.getTitle() == null) {
                        preparedStatement.setNull(3, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(3, movie.getTitle());
                    }
                    if (movie.getSubject() == null) {
                        preparedStatement.setNull(4, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(4, movie.getSubject());
                    }
                    if (movie.getActors() == null) {
                        preparedStatement.setNull(5, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(5, movie.getActors());
                    }
                    if (movie.getActress() == null) {
                        preparedStatement.setNull(6, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(6, movie.getActress());
                    }
                    if (movie.getDirector() == null) {
                        preparedStatement.setNull(7, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(7, movie.getDirector());
                    }
                    if (movie.getPopularity() == null) {
                        preparedStatement.setNull(8, Types.INTEGER);
                    } else {
                        preparedStatement.setInt(8, movie.getPopularity());
                    }
                    if (movie.getAwards() == null) {
                        preparedStatement.setNull(9, Types.BOOLEAN);
                    } else {
                        preparedStatement.setBoolean(9, movie.getAwards());
                    }
                    preparedStatement.executeUpdate();

                }
            }
        }
    }

}
