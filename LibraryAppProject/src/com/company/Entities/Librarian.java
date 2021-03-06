package com.company.Entities;

import com.company.Helpers.FileUtils;
import com.company.Library;
import com.company.Menus.AdminMenu;

import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.Helpers.Color.*;

public class Librarian extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    public Librarian(String name, String username, String password) {
        super(name, username, password);
    }

    public Librarian() {

    }

    public void librarianAddBook() {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter book title: ");
        String titleName = scan.nextLine();
        if (!isBookTitleValid(titleName)) {
            System.out.println("The book does already exist\n ");
        }
        while (titleName.isBlank() || titleName.isEmpty()) {
            System.out.println("Enter a book title, it can't be empty: ");
            titleName = scan.nextLine();
        }
        System.out.println("Enter a short description: ");
        String description = scan.nextLine();
        while (description.isBlank() || description.isEmpty()) {
            System.out.println("The description can't be empty, enter again:");
            description = scan.nextLine();
        }
        System.out.println("Enter author name: ");
        String author = scan.nextLine();
        while (author.isBlank() || author.isEmpty()) {
            System.out.println("The book needs to have an author! Enter name again:");
            author = scan.nextLine();
        }

        Book book = new Book(titleName, description, author);
        Library.getInstance().addBookToMap(book);
        System.out.println("\n" + GREEN + book.getTitle() + " by " + book.getAuthor() + " added to library" + RESET);
        FileUtils.writeObject(Library.getInstance().books, "src/com/company/Files/Books.ser");
    }


    public void librarianRemoveBookByChoice(Book bookToRemove) {
        if (bookToRemove.isAvailable()) {
            System.out.println(GREEN + "\n" + bookToRemove.getTitle() + " by " + bookToRemove.getAuthor() + " removed from system" + RESET);
            Library.getInstance().books.remove(bookToRemove.getTitle());
            FileUtils.writeObject(Library.getInstance().books, "src/com/company/Files/Books.ser");

        } else {
            System.out.println(RED + "\n" + bookToRemove.getTitle() + " is loaned out and cannot be removed" + RESET + bookToRemove.showDaysRemainingOnLoan() + "\n");
        }
    }

    public void librarianRemoveUserByChoice(User userToRemove) {
        if (userToRemove.getBooks().size() < 1) {
            System.out.println(GREEN + "\n" + userToRemove.getName() + " removed from system" + RESET);
            Library.getInstance().users.remove(userToRemove.getUsername());
            FileUtils.writeObject(Library.getInstance().users, "src/com/company/Files/User.ser");

        } else {
            System.out.println(RED + "\nThe user still got books borrowed and can therefore not be removed from the library." + RESET);

        }
    }

    public void librarianAddUser() {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter name: ");
        String name = scan.nextLine();
        while (!isNameValid(name)) {
            System.out.println(RED + "Enter name again," + RESET + " this time correct: ");
            name = scan.nextLine();
        }
        System.out.println("Enter username: ");
        String username = scan.nextLine();
        while (!isUsernameValid(username)) {
            System.out.println(RED + "A username can't contain white space!\n" + RESET +
                    "Please enter username again: ");
            username = scan.nextLine();
        }
        System.out.println("Enter password(minimum of 6 letters, including one number and one uppercase letter): ");
        String password = scan.nextLine();
        while (!isPasswordValid(password)) {
            System.out.println(RED + "Wrong, enter correct password format!\n" + RESET +
                    "(minimum of 6 letters, including one number and one uppercase letter):");
            password = scan.nextLine();
        }
        User user = new User(name, username, password);
        Library.getInstance().users.put(user.getName(), user);
        System.out.println(GREEN + "\nUser: " + user.getName() + " added to system" + RESET);
        FileUtils.writeObject(Library.getInstance().users, "src/com/company/Files/User.ser");
    }

    private boolean isPasswordValid(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=\\S+$).{6,20}$";
        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private boolean isUsernameValid(String username) {
        String regex = "^(?=\\S+$).{2,20}$";
        Pattern p = Pattern.compile(regex);
        if (username == null) {
            return false;
        }
        Matcher m = p.matcher(username);
        return m.matches();
    }

    private boolean isNameValid(String name) {
        String regex = "^[a-z??????A-Z??????\s]{3,40}" + "[^0-9]$";
        Pattern p = Pattern.compile(regex);
        if (name == null) {
            return false;
        }
        for (Person person : Library.getInstance().getUsersAsList()) {
            if (name.equalsIgnoreCase(person.getName())) {
                return false;
            }
        }
        Matcher m = p.matcher(name);
        return m.matches();
    }



    // Only shows user passwords to make development/testing easier
    public void seeAllUsers() {
        int i = 1;
        System.out.println();
        System.out.println(PURPLE + "---------------------------------------------------------------------------" + RESET);

        for (Person p : Library.getInstance().getUsersAsList()) {
            System.out.println("["+ i + "] " + YELLOW + p.getName() + CYAN + "\nUsername: " + RESET + p.getUsername() + CYAN + "\nPassword: " + RESET + p.getPassword() + CYAN +
                    "\nBorrowed books:" + RESET);
            p.setI(i);
            i++;

            for (Book book : ((User) p).getBooks()) {
                System.out.println("* " + YELLOW + book.getTitle() + RESET + book.showDaysRemainingOnLoan());
            }
            System.out.println(PURPLE + "---------------------------------------------------------------------------" + RESET);

        }

        Library.getInstance().getMenuHelper().getAdminMenuHandler().setAdminUserChoice(Library.getInstance().getUsersAsList());


    }

    public void seeAllBorrowedBooks() {
        System.out.println("\nThe following books are out on loan: ");
        for (Book book : Library.getInstance().getBooksAsList()) {
            if (!book.isAvailable()) {
                System.out.println("* " + YELLOW + book.getTitle() + RESET + " by " + book.getAuthor() + book.showDaysRemainingOnLoan());
            }
        }
    }

    public void searchForUserByName() {

        Scanner scan = new Scanner(System.in);
        System.out.println("\nType in a name: ");
        String regex = scan.nextLine();
        Pattern pa = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        System.out.println(YELLOW + "\n== NAMES OF USERS MATCHING: '" + regex + "' ==" + RESET);
        System.out.println(PURPLE + "---------------------------------------------------------------------------" + RESET);

        for (Person person : Library.getInstance().getUsersAsList()) {
            Matcher matcher = pa.matcher(person.getName());
            boolean matchFound = matcher.find();
            if (matchFound) {
                System.out.println(YELLOW + person.getName() + CYAN + "\nUsername: " + RESET + person.getUsername() + CYAN + "\nPassword: " + RESET + person.getPassword() + CYAN +
                        "\nBorrowed books:" + RESET);
                for (Book book : ((User) person).getBooks()) {
                    System.out.println("* " + YELLOW + book.getTitle() + RESET + book.showDaysRemainingOnLoan());
                }
                System.out.println(PURPLE + "---------------------------------------------------------------------------" + RESET);
            }
        }
    }

    public void showUser(Person person) {
        System.out.println(PURPLE + "\n---------------------------------------------------------------------------" + RESET);
        System.out.println(YELLOW + person.getName() + CYAN + "\nUsername: " + RESET + person.getUsername() + CYAN + "\nPassword: " + RESET + person.getPassword() + CYAN +
                "\nBorrowed books:" + RESET);
        for (Book book : ((User) person).getBooks()) {
            System.out.println("* " + YELLOW + book.getTitle() + RESET + book.showDaysRemainingOnLoan());
        }
        System.out.println(PURPLE + "---------------------------------------------------------------------------" + RESET);
        Library.getInstance().getMenuHelper().getAdminMenuHandler().adminUserMenu(person);


    }

    private boolean isBookTitleValid(String title) {
        for (Book book : Library.getInstance().getBooksAsList()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return false;
            }
        }
        return true;
    }
}