package silver.com;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

/**
* Library
*
* @author  Pavlo Melnyk
* @version 1.0
* @since   2016-22-11
 */
public class Library {
    private static final String URL = "jdbc:mysql://sql8.freemysqlhosting.net/sql8145927";
    private static final String USERNAME = "sql8145927";
    private static final String PASSWORD = "Ss43DEB6YT";
    private static final String INSERT = "INSERT INTO books (title, author) VALUES (?, ?)";
    private static final String GET_ALL = "SELECT title, author FROM books ORDER BY title";
    private static final String DELETE_OR_EDIT = "SELECT title, author FROM books WHERE title = ?";
    private static final String DELETE = "DELETE FROM books WHERE title = ? AND author = ?";
    private static final String UPDATE = "UPDATE books SET title = ? WHERE author = ?";

    private Connection connection;
    private PreparedStatement preparedStatement;

    public Library() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(String title, String author) throws SQLException {
        preparedStatement = connection.prepareStatement(INSERT);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.execute();
        preparedStatement.close();
        System.out.println("Book " + author + " \"" + title + "\" was added to library");
    }

    /**
     * Prints a list of all the books that are in the library
     *
     * @throws SQLException
     */
    public void getAllBooks() throws SQLException {
        preparedStatement = connection.prepareStatement(GET_ALL);

        ResultSet resultSet = preparedStatement.executeQuery();

        Book book = new Book();
        while (resultSet.next()) {
            book.setTitle(resultSet.getString("title"));
            book.setAuthor(resultSet.getString("author"));

            System.out.println(book);
        }
        preparedStatement.close();
    }

    /**
     * Find books with the same name in the library
     *
     * @param title
     *
     * @throws SQLException
     */
    private ArrayList<Book> duplicateFinder(String title) throws SQLException{
        preparedStatement = connection.prepareStatement(DELETE_OR_EDIT);
        ArrayList<Book> duplicateList = new ArrayList<Book>();

        preparedStatement.setString(1, title);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Book book = new Book();
            book.setTitle(resultSet.getString("title"));
            book.setAuthor(resultSet.getString("author"));
            duplicateList.add(book);
        }
        preparedStatement.close();
        return duplicateList;
    }

    /**
     * Renames book's title
     *
     * @param title The old book's title
     * @param newTitle The new book's title
     *
     * @return true if book wast edited
     *
     * @see #duplicateFinder(String)
     *
     * @throws SQLException
     * @throws IOException
     */
    public boolean editBook(String title, String newTitle) throws SQLException, IOException {
        ArrayList<Book> duplicateList = duplicateFinder(title);

        if (duplicateList.size() == 0) {
            System.out.println("This book is not available in the library");

            return false;
        } else if (duplicateList.size() == 1) {
            preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, newTitle);
            preparedStatement.setString(2, duplicateList.get(0).getAuthor());
            preparedStatement.executeUpdate();
            System.out.println("Book " + duplicateList.get(0).getTitle() +
                    "\"" + title + " \" was renamed to " + newTitle);
        } else {
            System.out.println("We have few books with such name please choose one by typing a number of book:");
            int i = 1;
            for (Book b : duplicateList) {
                System.out.println(i++ + ") " + b.getAuthor() + " \"" + b.getTitle() + "\"");
            }

            int choice = getChoice(duplicateList.size());

            preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, newTitle);
            preparedStatement.setString(2, duplicateList.get(choice - 1).getAuthor());
            preparedStatement.executeUpdate();
            System.out.println("Book " + duplicateList.get(choice - 1).getTitle() +
                    "\"" + title + " \" was renamed to " + newTitle);
        }
        preparedStatement.close();
        return true;
    }

    /**
     * Delete book from library
     *
     * @param title The book's title
     *
     * @see #duplicateFinder(String)
     *
     * @throws SQLException
     * @throws IOException
     */
    public boolean deleteBook(String title) throws SQLException, IOException{
        ArrayList<Book> duplicateList = duplicateFinder(title);

        if (duplicateList.size() == 0) {
            System.out.println("This book is not available in the library");

            return false;
        } else if (duplicateList.size() == 1) {
            preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setString(1, duplicateList.get(0).getTitle());
            preparedStatement.setString(2, duplicateList.get(0).getAuthor());
            preparedStatement.executeUpdate();
            System.out.println("Book " + duplicateList.get(0).getTitle() +
                    "\"" + title + " \" was remove from library");
        } else {
            System.out.println("We have few books with such name please choose one by typing a number of book:");
            int i = 1;
            for (Book b : duplicateList) {
                System.out.println(i++ + ") " + b.getAuthor() + " \"" + b.getTitle() + "\"");
            }

            int choice = getChoice(duplicateList.size());

            preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setString(1, duplicateList.get(choice - 1).getTitle());
            preparedStatement.setString(2, duplicateList.get(choice - 1).getAuthor());
            preparedStatement.executeUpdate();
            System.out.println("Book " + duplicateList.get(choice - 1).getTitle() +
                    " \"" + title + " \" was removed from library");
        }

        preparedStatement.close();
        return true;
    }

    private int getChoice(int size) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int choice;

        try {
            choice = Integer.parseInt(reader.readLine());
            if (!(choice <= size && choice > 0)) {
                System.out.println("Enter correct choice");

                choice = getChoice(size);
            }
        } catch (NumberFormatException e) {
            System.out.println("Enter correct value");
            choice = getChoice(size);
        }

        return choice;
    }
}