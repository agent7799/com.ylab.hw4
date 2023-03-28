package lesson04.eventsourcing.api;

import lesson04.eventsourcing.Person;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;


public interface PersonApi {
  void deletePerson(Long personId) throws IOException, TimeoutException;

  void savePerson(Long personId, String firstName, String lastName, String middleName) throws IOException, TimeoutException;

  Person findPerson(Long personId) throws SQLException;

  List<Person> findAll() throws SQLException;
}
