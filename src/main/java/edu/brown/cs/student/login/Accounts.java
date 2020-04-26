package edu.brown.cs.student.login;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Static utility class that deals with login generation, storing, and attempts for all Accounts.
 */
public class Accounts {
  private static final Random RANDOM = new SecureRandom();
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;
  private static final int SALT_LENGTH = 16;
  private static final String LOGIN_INFO_PATH = "src/main/resources/login/account-login-info.csv";
  private static Map<String, User> nameUserMap;

  /**
   * static utility class, don't instantiate except for testing.
   */
  protected Accounts() { }

  /**
   * getter.
   * @return map
   */
  public Map<String, User> getNameUserMap() {
    return this.getNameUserMap();
  }

  /**
   * get a specific User.
   * @param username - name
   * @return the User
   */
  public static User getUser(String username) {
    return nameUserMap.get(username);
  }

  /**
   * adds previously added users to the map; to be called at the start of running application.
   * @throws AccountException on file failure
   */
  public static void initializeMap() throws AccountException {
    initializeMap(LOGIN_INFO_PATH);
  }
  public static void initializeMap(String path) throws AccountException {
    nameUserMap = new HashMap<>();
    // create users from info files and databases
    try (Scanner loginInfo = new Scanner(new FileReader(path))) {
      // create each user
      while (loginInfo.hasNext()) {
        String[] login = loginInfo.nextLine().split(",");
        String username = login[0];
        User user = new User(username);
        nameUserMap.put(username, user);
      }
    } catch (FileNotFoundException e) {
      throw new AccountException(e.getMessage(), e);
    }
  }

  /**
   * reads the first line of the csv, for testing / checking files for proper format.
   * @return
   * @throws AccountException
   */
  public static String readHeader() throws AccountException {
    return readHeader(LOGIN_INFO_PATH);
  }
  // implementation
  public static String readHeader(String path) throws AccountException {
    try (Scanner loginInfo = new Scanner(new FileReader(path))) {
      if (loginInfo.hasNext()) {
        return loginInfo.next();
      } else {
        throw new AccountException("header of login info file does not exist");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    throw new AccountException("login info file does not exist");
  }


  /**
   * Stores a user's login info securely by encoding the password using salt hashing. Writes user,
   * password hash, salt to a csv.
   * @param user
   * @param pass
   * @throws AccountException
   */
  protected static void writeLoginInfo(String user, String pass) throws AccountException {
    writeLoginInfo(user, pass, BCrypt.gensalt(), LOGIN_INFO_PATH);
  }
  // actual computation
  protected static void writeLoginInfo(String user, String pass, String salt, String path) throws AccountException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
      String hash = BCrypt.hashpw(pass, salt);
      writer.write("\n" + user + "," + hash + "," + salt);
    } catch (IOException e) {
      throw new AccountException(e.getMessage(), e);
    }
  }
  // for testing
  protected static void writeLoginInfo(String user, String pass, String path) throws AccountException {
    writeLoginInfo(user, pass, BCrypt.gensalt(), path);
  }

  /**
   * checks if a username and login pair are stored in the csv (user exists).
   * @param inpUser username
   * @param inpPass password
   * @return text output for repl/gui
   * @throws AccountException for file errors
   */
  public static String checkLogin(String inpUser, String inpPass) throws AccountException {
    return checkLogin(inpUser, inpPass, LOGIN_INFO_PATH);
  }
  // for repl, reads in username and password from user keyboard (System.in)
  public static String checkLogin() throws AccountException {
    try (Scanner keyboard = new Scanner(System.in)) {
      // get input from user
      System.out.println("Username: ");
      String inpUser = keyboard.nextLine();
      System.out.println("Password: ");
      String inpPass = keyboard.nextLine();
      return checkLogin(inpUser, inpPass, LOGIN_INFO_PATH);
    }
  }
  // computation
  public static String checkLogin(String inpUser, String inpPass, String path) throws AccountException {
    try (Scanner loginInfo = new Scanner(new FileReader(path))) {
      String[] login;
      String user;
      String passHash;
      String salt;
      // check each entry of login info csv to see if there's a match
      while (loginInfo.hasNext()) {
        login = loginInfo.nextLine().split(",");
        user = login[0];
        passHash = login[1];
        salt = login[2];
        // check if user and pass match
        if (user.equals(inpUser) && passHash.equals(BCrypt.hashpw(inpPass, salt))) {//BCrypt.checkpw(inpPass, passHash)
          return login(user);
        }
      }
    } catch (FileNotFoundException e) {
      throw new AccountException(e.getMessage(), e);
    }
    // none of the lines fit
    return "Failed Login: Please try again.";
  }

  /**
   * handles logging and getting user specific data.
   * @param user username
   * @return text output
   */
  public static String login(String user) {
    // give access to data of user for future commands
    return user + " successfully Logged in!";
  }
  
  
  /**
   * Method for signup validity.
   * @param user
   * @param pass1
   * @param pass2
   * @return
   */
  public static boolean checkSignUpValidity(String user, String pass1, String pass2) {
    // check if user already exists
    // check if passwords are same
    if(!userExists(user) && comparePasswords(pass1, pass2) &&
        checkInputExists(user, pass1, pass2)) {
      return true;
    } else {
      return false;
    }
    // TODO: informative error messages for each specific signup error
  }

  private static boolean checkInputExists(String user, String pass1, String pass2) {
    if((user.length() > 0) && (pass1.length() > 0) && (pass2.length() > 0)) {
      return true;
    } else {
      return false;
    }
  }

  private static boolean userExists(String user) {
    if (nameUserMap.containsKey(user)) {
      return true;
    } else {
      return false;
    }
  }

  private static boolean comparePasswords(String pass1, String pass2) {
    if (pass1.equals(pass2)) {
      return true;
    } else {
      return false;
    }
  }
}
