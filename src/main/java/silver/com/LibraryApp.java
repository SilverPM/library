package silver.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

/**
 * Created by Pavlo Melnyk on 22.11.2016.
 */
public class LibraryApp {
    public static void main(String[] args) throws SQLException, IOException {
        Library library = new Library();

        boolean running = true;

        while (running) {
            System.out.println("\nEnter 0 for show all books in library");
            System.out.println("Enter 1 for add book to library");
            System.out.println("Enter 2 for edit book");
            System.out.println("Enter 3 for delete book from library");
            System.out.println("Enter 4 for quit");
            int chioce = getChoice();

            switch (chioce) {
                case 0:
                    library.getAllBooks();
                    break;
                case 1:
                    System.out.print("Enter Title: ");
                    String title = getString();
                    System.out.print("Enter Author: ");
                    String author = getString();
                    library.addBook(title, author);
                    break;
                case 2:
                    System.out.print("Enter book's title which you want to edit: ");
                    String oldtitle = getString();
                    System.out.print("Enter new book's title: ");
                    String newTitle = getString();
                    library.editBook(oldtitle, newTitle);
                    break;
                case 3:
                    System.out.print("Enter book's title which you want to delete: ");
                    String delTitle = getString();
                    library.deleteBook(delTitle);
                    break;
                case 4:
                    running = false;
                default:
                    System.out.println("Error! Enter correct number");
                    break;
            }
        }
    }

    private static int getChoice() throws IOException {
       int choice = 0;
        try {
           BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           choice = Integer.parseInt(reader.readLine());
       } catch (NumberFormatException e) {
            System.out.println("Enter correct value");
            choice = getChoice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return choice;
    }

    private static String getString() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
