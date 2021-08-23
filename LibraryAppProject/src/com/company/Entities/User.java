package com.company.Entities;

import com.company.Helpers.FileUtils;
import com.company.Library;
import com.company.Menus.UserMenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.company.Helpers.Color.*;

public class User extends Person implements Serializable {

    private static final long serialVersionUID = 2L;
    private int i;

    public List<Book> userBooks = new ArrayList<>();

    public User(String name, String username, String password) {
        super(name, username, password);
    }

    public List<Book> getBooks() {
        return userBooks;
    }

    public void borrowBook(Book book) {
        if (book.isAvailable()) {
            userBooks.add(book);
            System.out.println(GREEN + "\n" + getName() + " loans " + book.getTitle() + RESET);
            book.setAvailable(false);
            FileUtils.writeObject(Library.getInstance().books, "src/com/company/Files/Books.ser");
            FileUtils.writeObject(Library.getInstance().users, "src/com/company/Files/User.ser");

        } else {
            System.out.println(RED + "\n" + book.getTitle() + " is already loaned out" + RESET);
        }
    }

    public void returnBook(Book bookToReturn) {
        userBooks.remove(bookToReturn);
        bookToReturn.setAvailable(true);
        System.out.println(GREEN + "\n" + bookToReturn.getTitle() + " by " + bookToReturn.getAuthor() + " returned to library" + RESET);

        FileUtils.writeObject(Library.getInstance().books, "src/com/company/Files/Books.ser");
        FileUtils.writeObject(Library.getInstance().users, "src/com/company/Files/User.ser");
    }

    public void showUserBooks() {
        int i = 1;
        userBooks.sort(Comparator.comparing(Book::getTitle));
        System.out.println();

        if (userBooks.size() != 0) {

            for (Book book : userBooks) {
                book.setI(i);
                System.out.println(CYAN + "[" + i + "] " + YELLOW + book.getTitle().toUpperCase() + RESET + book.showDaysRemainingOnLoan());
                i++;
            }
            Library.getInstance().getMenuHelper().selectBookOption(UserMenu.values(), userBooks);

        } else {
            System.out.println(GREEN + "You don´t have any loans" + RESET);
            Library.getInstance().getMenuHelper().generalReturnMenu(UserMenu.values());
        }
    }

    @Override
    public String toString() {
        return getName() + ", " + getUsername() + ", " + getPassword();
    }
}