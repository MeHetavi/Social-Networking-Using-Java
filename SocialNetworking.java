
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

class SocialNetworking {

    // ------------------------------------------------------------------------------------------------------

    static Scanner sc = new Scanner(System.in);
    static HashSet<Node> users = new HashSet<>();
    static Node activeUser;
    static String dburl = "jdbc:mysql://localhost:3306/Social Networking", dbuser = "root", dbpass = "";

    // -------------------------------------------------------------------------------------------------------

    static void getUsers() throws SQLException {

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Social Networking", "root", "");
        Statement getUsers = con.createStatement();
        ResultSet allUsers = getUsers.executeQuery("SELECT * FROM USERS;");

        while (allUsers.next()) {

            Node n = new Node(allUsers.getString(1), allUsers.getString(2), allUsers.getString(3),
                    allUsers.getString(4), allUsers.getString(5), allUsers.getString(6),
                    allUsers.getString(7), allUsers.getInt(8), allUsers.getInt(9), allUsers.getLong(10));
            if (activeUser != null && n.userId.equals(activeUser.userId)) {
                activeUser = n;
            }
            users.add(n);
        }

    }

    // --------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws SQLException, IOException {

        Scanner sc = new Scanner(System.in);
        getUsers();

        l1: // To break below infinite loop after signing up or loging in.
        for (;;) {

            System.out.println("\n\u001B[34m          GREETINGS!\u001B[0m\n");

            System.out.println("1.Sign Up\n2.Log In\n3.Exit");// switch1 --> to activate user
            System.out.print("Enter Your Choice : ");
            switch (sc.nextInt()) {
                case 1:
                    System.out.println("\n-----ENTER YOUR CREDENTIALS----- \n");

                    signUp();
                    System.out.println(
                            "\u001B[32mAccount Created Successfully\n\nLogged In As "
                                    + activeUser.name.toUpperCase() + "\u001B[0m\n\n");

                    break l1;

                case 2:
                    System.out.println("\n-----ENTER YOUR CREDENTIALS----- \n");

                    if (!logIn()) {
                        System.out.println("Log In Failed.");
                        System.out.print("\n\n");
                        main(args);
                    }
                    break l1;
                case 3:
                    System.out.println("\n\u001B[35m        THANK YOU!\u001B[0m\n");
                    System.exit(0);

                default:
                    System.out.println("Enter a valid choice.");
            }
        }

        insideTheApp();
    }

    // ACCOUNT VALIDATION ----------------------------------------------------------

    static void signUp() throws SQLException {
        System.out.print("Enter Name : ");
        String name = sc.nextLine();
        for (;;) {
            System.out.print("\nEnter Mobile Number : ");
            long mobileNumber = sc.nextLong();
            if (validateMobileNumber(mobileNumber)) {
                for (;;) {

                    System.out.print("\nCreate UserId : ");
                    String userId = sc.next();
                    System.out.println();
                    if (validateUserId(userId)) {
                        for (;;) {

                            System.out.println(
                                    "\nPassword must be of 6 characters only.\nNo spaces are allowed in the password.\n");
                            System.out.print("\nEnter password : ");
                            String password1 = sc.next();
                            System.out.println();

                            if (validatePassword(password1)) {
                                System.out.print("Confirm Password : ");
                                String password2 = sc.next();
                                System.out.println();

                                if (password2.equals(password1)) {
                                    Node n = new Node(userId, password1, name, "null", "null", "null", "null", 0, 0,
                                            mobileNumber);
                                    users.add(n);// So the newly added user also gets added in users hashSet
                                    activeUser = n;
                                    Connection con = DriverManager
                                            .getConnection("jdbc:mysql://localhost:3306/Social Networking", "root", "");
                                    Statement insertNewUser = con.createStatement();
                                    insertNewUser
                                            .execute("INSERT INTO USERS VALUES('" + userId + "','" + password1 + "','"
                                                    + name + "','null','null','null','null',0,0," + mobileNumber
                                                    + ");");
                                    // Node inserted into users table
                                    return;
                                } else {
                                    System.out.println("password 1 and 2 do not match.\n");
                                }
                            }

                            else {
                                System.out.println("Invalid password type.\nTry again\n");
                            }
                        }

                    } else {
                        System.out.println("Sorry, This userId is already taken");
                        System.out.println("Try Entering With some Other Id");
                    }
                }

            } else {
                System.out.println("Enter A Valid mobile number.");
            }
        }

    }

    static boolean logIn() throws SQLException {
        if (validateAccount()) {
            System.out
                    .println("\u001B[32mLogged in successfully As " + activeUser.name.toUpperCase() + "\u001B[0m\n\n");
            return true;
        } else {

            System.out.println("Invalid Account");
            return false;
        }
    }

    static boolean validateUserId(String userId) throws SQLException {
        for (Node user : users) {
            if (user.userId.equals(userId))// To check if user_id does not exist already
                return false;
        }
        return true;

    }

    static boolean validatePassword(String password) {
        if (password.toCharArray().length != 6)
            return false;
        else if (password.contains(" "))
            return false;

        return true;
    }

    static void viewProfileOrReturn(HashMap<Integer, Node> searchResults) throws SQLException, IOException {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);

        System.out.println("\n\n1.View a profile from The Search\n2.Return");
        System.out.print("Enter Your Choice : ");
        switch (sc.nextInt()) {// switch of search block(switch-3)

            case 1:// .View a profile from The Search
                System.out.println("\n\n");
                System.out.print("Enter profile number to be visited : ");
                int count = sc.nextInt();
                if (searchResults.get(count) != null) {

                    Node profile = searchResults.get(count);
                    System.out.println("\n----------------------------------");
                    System.out.println("User Id : " + profile.userId);
                    System.out.println("----------------------------------");
                    System.out.println("Name : " + profile.name);
                    System.out.println("----------------------------------");
                    System.out.println("Followers : " + profile.followersCount);
                    System.out.println("----------------------------------");
                    System.out.println("Following :" + profile.followingCount);
                    System.out.println("----------------------------------");

                    // Mutual followers
                    if ((activeUser.following1_UserId.equals(profile.following1_UserId)
                            || activeUser.following2_UserId.equals(profile.following1_UserId))
                            && !profile.following1_UserId.equals("null")) {
                        System.out.println("Mutual : " + profile.following1_UserId);
                        System.out.println("----------------------------------");

                    }
                    if ((activeUser.following1_UserId.equals(profile.following2_UserId)
                            || activeUser.following2_UserId.equals(profile.following2_UserId))
                            && !profile.following2_UserId.equals("null")) {
                        System.out.println("Mutual : " + profile.following2_UserId);
                        System.out.println("----------------------------------");

                    }
                    System.out.println();
                    System.out.println();

                    if (activeUserFollowsProfile(activeUser, profile)) { // Check Here.
                        l4: for (;;) {

                            System.out.println("1.Unfollow\n2.Message\n3.Return\n");
                            System.out.print("Enter Your Choice : ");
                            switch (sc.nextInt()) {

                                case 1:
                                    if (unfollow(activeUser, profile)) {
                                        System.out.println("Unfollowed " + profile.name + "\n\n");

                                        PreparedStatement updateFollowingsCount = con.prepareStatement(
                                                "UPDATE USERS SET FOLLOWING_COUNT = ? WHERE USER_iD = ?");
                                        updateFollowingsCount.setInt(1, activeUser.followingCount - 1);
                                        updateFollowingsCount.setString(2, activeUser.userId);
                                        updateFollowingsCount.execute();

                                        PreparedStatement updateFollowersCount = con.prepareStatement(
                                                "UPDATE USERS SET FOLLOWers_COUNT = ? WHERE USER_iD = ?");
                                        updateFollowersCount.setInt(1, profile.followersCount - 1);
                                        updateFollowersCount.setString(2, profile.userId);
                                        updateFollowersCount.execute();

                                        users.clear();
                                        getUsers();
                                    }
                                    break l4;
                                case 2:
                                    if (activeUserFollowsProfile(profile, activeUser)) {
                                        if (new File(activeUser.userId + profile.userId + ".txt").exists()) {
                                            messages(profile, activeUser.userId + profile.userId + ".txt");
                                        } else {
                                            messages(profile, profile.userId + activeUser.userId + ".txt");
                                        }
                                    } else
                                        System.out.println("Can't Talk.\n");
                                    break;
                                case 3:// Returns from switch-3 and returns ultimately to switch-2.
                                    break l4;

                                default:
                                    System.out.println("Enter a valid choice.");
                            }
                        }

                    } else {
                        l5: for (;;) {

                            System.out.println("1.Follow\n2.Return");
                            System.out.println(
                                    "\u001B[33mYou Need To Follow This Profile To Unlock Chat With Them.\u001B[0m\n");

                            System.out.print("Enter Your Choice : ");
                            switch (sc.nextInt()) {
                                case 1:// To follow the profile
                                    if (follow(activeUser, profile)) {
                                        System.out.println("Following " + profile.name + "\n\n");

                                        // Update followings count of activeUser.
                                        PreparedStatement updateFollowingsCount = con
                                                .prepareStatement(
                                                        "UPDATE USERS SET FOLLOWING_COUNT = ? WHERE USER_ID = ?");
                                        updateFollowingsCount.setInt(1, activeUser.followingCount + 1);
                                        updateFollowingsCount.setString(2, activeUser.userId);
                                        updateFollowingsCount.execute();

                                        // Update followers count of profile.
                                        PreparedStatement updateFollowersCount = con
                                                .prepareStatement(
                                                        "UPDATE USERS SET FOLLOWERS_COUNT = ? WHERE USER_ID = ?");
                                        updateFollowersCount.setInt(1, profile.followersCount + 1);
                                        updateFollowersCount.setString(2, profile.userId);
                                        updateFollowersCount.execute();

                                        users.clear();
                                        getUsers();
                                    }
                                    break l5;

                                case 2:// exits from switch-3 and returns ultimately to switch-2.
                                    System.out.println();
                                    break l5;

                                default:
                                    System.out.println("Enter a valid choice.");
                            }
                        }
                    }
                } else {
                    System.out.println("Not a valid number.\n\n");
                }
                break;

            case 2:// returns to switch-2.
                break;

            default:
                System.out.println("Enter a valid choice.");
                viewProfileOrReturn(searchResults);
        }

    }

    static boolean validateMobileNumber(long mobileNumber) {// To check if mobile number entered is valid or not.
        if (mobileNumber >= 1000000000l && mobileNumber <= 9999999999l) {
            return true;
        }
        return false;
    }

    static boolean validateAccount() throws SQLException {
        boolean userIdMAtched = false;
        for (int i = 1; i <= 3; i++) {
            System.out.print("Enter User Id : ");
            String userId = sc.next();
            for (Node user : users) {

                if (user.userId.equals(userId)) {
                    userIdMAtched = true;
                    System.out.print("\nEnter Password : ");
                    String password = sc.next();

                    if (user.password.equals(password)) {
                        activeUser = user;
                        return true;

                    } else {
                        System.out.println("\nInvalid Password.\n");
                        System.out.println("1.Forgot Password\n2.Try again");
                        System.out.print("Enter Your Choice : ");
                        switch (sc.nextInt()) {
                            case 1:
                                System.out.print("Enter Mobile No : ");
                                long mobileNumber = sc.nextLong();
                                if (user.mobileNumber == mobileNumber) {
                                    if (forgotPassword(mobileNumber, user)) {
                                        logIn();
                                    }
                                } else {
                                    System.out.println("\nMobile Number Does Not Match.\n");
                                    return false;
                                }

                                break;
                            case 2:
                                System.out.print("\nEnter Password : ");
                                String password1 = sc.next();

                                if (user.password.equals(password1)) {
                                    activeUser = user;
                                    return true;
                                } else {
                                    System.out.println("\nInvalid Password.\n");
                                }
                                break;

                            default:
                                System.out.println("Enter a valid choice.");
                        }
                    }
                }

            }
            if (userIdMAtched == false) {
                System.out.println("Invalid User Id,\nTry Entering Again.\n");
            }

        }
        System.out.println("Invalid User Id or password.");
        System.out.println();
        return false;

    }

    // Recover Password -----------------------------------------------------

    static boolean forgotPassword(long mobileNumber, Node user) throws SQLException {
        System.out.println("Password must be of 8 characters only.\nNo spaces are allowed in the password.");
        System.out.print("Enter New Password : ");
        String password1 = sc.next();

        if (validatePassword(password1)) {
            System.out.print("Confirm Password : ");
            String password2 = sc.next();

            System.out.println();
            if (password2.equals(password1)) {

                user.password = password1;// Change password
                System.out.println("Password changed succesfully.");
                Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
                Statement updatePassword = con.createStatement();
                updatePassword.execute(
                        "UPDATE USERS SET PASSWORD = '" + password1 + "' WHERE MOBILE_NUMBER = " + mobileNumber);
                return true;

            } else {
                System.out.println("Password 1 and 2 do not match.");
            }

        } else {
            System.out.println("Password criteria is not fulfilled");
        }

        System.out.println("Password updation failed.");
        return false;

    }

    // Inside The App -------------------------------------------------------

    static void insideTheApp() throws SQLException, IOException {
        for (;;) {
            System.out.print("\n\n\u001B[32mWELCOME, " + activeUser.name.toUpperCase() + "\u001B[0m \n\n");

            System.out.println("1.Search\n2.Explore\n3.Chat\n4.Your Profile\n5.Log out\n6.Delete Account\n");
            System.out.print("Enter Your Choice : ");
            int choice = sc.nextInt();

            users.clear();
            getUsers();

            switch (choice) {// Switch2 --> functions after logging in.

                case 1:// Search
                    System.out.print("\nEnter Text : ");
                    String s = sc.next();
                    System.out.println();
                    HashMap<Integer, Node> searchResults = searchUsers(s);
                    if (searchResults != null)
                        viewProfileOrReturn(searchResults);
                    else
                        System.out.println("No Accounts Related To Search Were Found.");
                    break;

                case 2: // code to Explore.
                    HashMap<Integer, Node> exploreResults = searchUsers("");
                    viewProfileOrReturn(exploreResults);
                    break;
                case 3:
                    System.out.print("\nEnter Profile Id : ");
                    String name = sc.next();
                    HashMap<Integer, Node> chatSearchResults = searchUsers(name);
                    if (chatSearchResults != null)
                        viewProfileOrReturn(chatSearchResults);
                    else
                        System.out.println("No Accounts Related To Search Were Found.");
                    break;
                case 4:
                    yourProfile();
                    // System.out.print("1.Update Profile\n2.Return\nEnter Your Choice : ");
                    // switch (sc.nextInt()) {

                    // case 1:
                    // System.out.print(
                    // "1.Change Name\n2.Change UserId\n3.Change Password\n4.Exit\nEnter Your Choice
                    // : ");
                    // switch(sc.nextInt()){
                    // case 1 : System.out.println("Enter Name : ");
                    // }
                    // }
                    break;

                case 5: // log out

                    System.out.println("\nLogged Out Successfully.\n");
                    activeUser = null;
                    sc.nextLine();
                    main(null);

                    break;

                case 6: // Code to delete account
                    System.out.println("\nOnce You Delete Your Account,All Your Data Will Be Lost.\n");
                    System.out.print("Enter Password To Proceed : ");
                    if (sc.next().equals(activeUser.password)) {
                        deleteAccountOfActiveUser();
                        System.out.println("Account Deleted Successfully.\n\n");
                        sc.nextLine();
                        main(null);
                    }
                    System.out.println("\nInvalid Password.");
                    System.out.println("Account Was Not Deleted.\n\n");
                    break;

                default:
                    System.out.println("Enter a valid choice.");
            }
        }
    }

    // FOLLOW/UNFOLLOW----------------------------------------------------------------------------

    static boolean activeUserFollowsProfile(Node activeUser, Node profile) {// Check here

        if (activeUser.following1_UserId.equals(profile.userId)) {
            return true;
        }

        else if (activeUser.following2_UserId.equals(profile.userId)) {
            return true;
        }
        return false;
    }

    static boolean follow(Node activeUser, Node profile) throws SQLException {// activeUser follows the profile

        users.clear();
        getUsers();

        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);

        if (activeUser.following1_UserId.equals("null")) {

            // update followings of profile.
            PreparedStatement setFollowings = con
                    .prepareStatement("UPDATE USERS SET FOLLOWING1 = ? WHERE USER_ID = ?");

            if (profile.follower1_UserId.equals("null")) {

                // UPDATE followings of active user.
                setFollowings.setString(1, profile.userId);
                setFollowings.setString(2, activeUser.userId);
                setFollowings.execute();

                // update followers of profile
                PreparedStatement setFollowers = con
                        .prepareStatement("UPDATE USERS SET FOLLOWER1 = ? WHERE USER_ID = ?");
                setFollowers.setString(1, activeUser.userId);
                setFollowers.setString(2, profile.userId);
                setFollowers.execute();

                users.clear();
                getUsers();
                return true;

            } else if (profile.follower2_UserId.equals("null")) {

                // update followings of Active User.
                setFollowings.setString(1, profile.userId);
                setFollowings.setString(2, activeUser.userId);
                setFollowings.execute();

                // update followers of profile
                PreparedStatement setFollowers = con
                        .prepareStatement("UPDATE USERS SET FOLLOWER2 = ? WHERE USER_ID = ?");
                setFollowers.setString(1, activeUser.userId);
                setFollowers.setString(2, profile.userId);
                setFollowers.execute();

                users.clear();
                getUsers();
                return true;

            } else {
                System.out.println("Profile cannot be followed as it already has 2 followers.");
                return false;
            }

        } else if (!activeUser.following2_UserId.equals("null")) {

            System.out.println("Profile cannot be followed.");
            return false;
        } else {
            PreparedStatement setFollowings = con
                    .prepareStatement("UPDATE USERS SET FOLLOWING2 = ? WHERE USER_ID = ?");

            if (profile.follower1_UserId.equals("null")) {

                // update followings of activeUser.
                setFollowings.setString(1, profile.userId);
                setFollowings.setString(2, activeUser.userId);
                setFollowings.execute();

                // update followers of profile
                PreparedStatement setFollowers = con
                        .prepareStatement("UPDATE USERS SET FOLLOWER1 = ? WHERE USER_ID = ?");
                setFollowers.setString(1, activeUser.userId);
                setFollowers.setString(2, profile.userId);
                setFollowers.execute();

                users.clear();
                getUsers();
                return true;

            } else if (profile.follower2_UserId.equals("null")) {

                // update followings of profile.
                setFollowings.setString(1, profile.userId);
                setFollowings.setString(2, activeUser.userId);
                setFollowings.execute();

                // update followers of profile
                PreparedStatement setFollowers = con
                        .prepareStatement("UPDATE USERS SET FOLLOWER2 = ? WHERE USER_ID = ?");
                setFollowers.setString(1, activeUser.userId);
                setFollowers.setString(2, profile.userId);
                setFollowers.execute();

                users.clear();
                getUsers();
                return true;
            } else {
                System.out.println("Profile cannot be followed as it already has 2 followers.");
                return false;

            }
        }
    }

    static boolean unfollow(Node activeUser, Node profile) throws SQLException {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);

        if (activeUser.following1_UserId.equals(profile.userId)) {
            // update following-1 of profile.

            PreparedStatement setFollowings = con
                    .prepareStatement("UPDATE USERS SET FOLLOWING1 = ? WHERE USER_ID = ?");

            setFollowings.setString(1, "null");
            setFollowings.setString(2, activeUser.userId);
            setFollowings.execute();

        } else if (activeUser.following2_UserId.equals(profile.userId)) {
            // update following-2 of activeUser.

            PreparedStatement setFollowings = con
                    .prepareStatement("UPDATE USERS SET FOLLOWING2 = ? WHERE USER_ID = ?");

            setFollowings.setString(1, "null");
            setFollowings.setString(2, activeUser.userId);
            setFollowings.execute();

        }

        if (profile.follower1_UserId.equals(activeUser.userId)) {
            // update followers of profile

            PreparedStatement setFollowers = con
                    .prepareStatement("UPDATE USERS SET FOLLOWER1 = ? WHERE USER_ID = ?");
            setFollowers.setString(1, "null");
            setFollowers.setString(2, profile.userId);
            setFollowers.execute();

        } else if (profile.follower2_UserId.equals(activeUser.userId)) {
            // update followers of profile

            PreparedStatement setFollowers = con
                    .prepareStatement("UPDATE USERS SET FOLLOWER2 = ? WHERE USER_ID = ?");
            setFollowers.setString(1, "null");
            setFollowers.setString(2, profile.userId);
            setFollowers.execute();

        }

        users.clear();
        getUsers();
        return true;

    }

    // SURFING ----------------------------------------------------------

    static HashMap<Integer, Node> searchUsers(String userId) {
        int count = 0; // To count number of related users
        System.out.println("\n-----Related Users-----\n");
        HashMap<Integer, Node> searchResults = new HashMap<>();// To save the users related to search

        for (Node user : users) {
            if (!user.userId.equals(activeUser.userId)) {

                if (user.userId.contains(userId)) {
                    System.out.println("----------" + (++count) + "----------");
                    System.out.println("User Id : " + user.userId);
                    System.out.println("Name : " + user.name);
                    System.out.println("Followers : " + user.followersCount);
                    System.out.println("Following :" + user.followingCount);
                    // Mutual Followers
                    if ((activeUser.following1_UserId.equals(user.following1_UserId)
                            || activeUser.following2_UserId.equals(user.following1_UserId))
                            && !user.following1_UserId.equals("null")) {
                        System.out.println("Mutual : " + user.following1_UserId);
                    }
                    if ((activeUser.following1_UserId.equals(user.following2_UserId)
                            || activeUser.following2_UserId.equals(user.following2_UserId))
                            && !user.following2_UserId.equals("null")) {
                        System.out.println("Mutual : " + user.following2_UserId);
                    }
                    System.out.println();
                    searchResults.put(count, user);
                }
            }

        }
        if (count == 0) {
            System.out.println("   No Results Found");
        }
        return searchResults;
    }

    // DELETE THE ACCOUNT ---------------------------------------------------------

    static void deleteAccountOfActiveUser() throws SQLException {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);

        // Statement updateFollows = con.createStatement();
        HashMap<String, Node> usersHM = new HashMap<>();
        for (Node user : users) {
            usersHM.put(user.userId, user);
        }

        if (!activeUser.following1_UserId.equals("null")) {

            PreparedStatement updateFollowersCount = con.prepareStatement(
                    "UPDATE USERS SET FOLLOWers_COUNT = ? WHERE USER_iD = ?");
            updateFollowersCount.setInt(1, usersHM.get(activeUser.following1_UserId).followersCount - 1);
            updateFollowersCount.setString(2, usersHM.get(activeUser.following1_UserId).userId);
            updateFollowersCount.execute();
            unfollow(activeUser, usersHM.get(activeUser.following1_UserId));

        }
        if (!activeUser.following2_UserId.equals("null")) {

            PreparedStatement updateFollowersCount = con.prepareStatement(
                    "UPDATE USERS SET FOLLOWers_COUNT = ? WHERE USER_iD = ?");
            updateFollowersCount.setInt(1, usersHM.get(activeUser.following2_UserId).followersCount - 1);
            updateFollowersCount.setString(2, usersHM.get(activeUser.following2_UserId).userId);
            updateFollowersCount.execute();

            unfollow(activeUser, usersHM.get(activeUser.following2_UserId));

        }
        if (!activeUser.follower1_UserId.equals("null")) {

            PreparedStatement updateFollowingsCount = con.prepareStatement(
                    "UPDATE USERS SET FOLLOWING_COUNT = ? WHERE USER_iD = ?");
            updateFollowingsCount.setInt(1, usersHM.get(activeUser.follower1_UserId).followingCount - 1);

            updateFollowingsCount.setString(2, usersHM.get(activeUser.follower1_UserId).userId);
            updateFollowingsCount.execute();
            unfollow(usersHM.get(activeUser.follower1_UserId), activeUser);

        }
        if (!activeUser.follower2_UserId.equals("null")) {

            PreparedStatement updateFollowingsCount = con.prepareStatement(
                    "UPDATE USERS SET FOLLOWING_COUNT = ? WHERE USER_iD = ?");
            updateFollowingsCount.setInt(1, usersHM.get(activeUser.follower2_UserId).followingCount - 1);

            updateFollowingsCount.setString(2, usersHM.get(activeUser.follower2_UserId).userId);
            updateFollowingsCount.execute();

            PreparedStatement updateFollowersCount = con.prepareStatement(
                    "UPDATE USERS SET FOLLOWers_COUNT = ? WHERE USER_iD = ?");
            updateFollowersCount.setInt(1, activeUser.followersCount - 1);
            updateFollowersCount.setString(2, activeUser.userId);
            updateFollowersCount.execute();
            unfollow(usersHM.get(activeUser.follower2_UserId), activeUser);

        }
        PreparedStatement deleteActiveUser = con.prepareStatement("DELETE FROM USERS WHERE USER_ID = ?");
        deleteActiveUser.setString(1, activeUser.userId);
        deleteActiveUser.execute();

        users.clear();
        getUsers();
    }

    // DISPLAY THE PROFILE INFORMATION ------------------------------------------

    static void yourProfile() {
        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println();

        for (int i = 0; i <= 14; i++) {
            System.out.print(" ");
        }

        System.out.print("YOUR PROFILE");

        for (int i = 0; i <= 14; i++) {
            System.out.print(" ");
        }

        System.out.println();

        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println();
        System.out.print("Name : " + activeUser.name.toUpperCase() + "\n");

        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println();

        System.out.print("User Id : " + activeUser.userId + "\n");

        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println();

        if (!activeUser.following1_UserId.equals("null")) {
            System.out.print("Following : " + activeUser.following1_UserId);

        }
        if (!activeUser.following2_UserId.equals("null")) {
            System.out.print("," + activeUser.following2_UserId + "\n");

        }
        System.out.println();
        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }
        System.out.println();

        if (!activeUser.follower1_UserId.equals("null")) {
            System.out.print("Follower : " + activeUser.follower1_UserId);

        }
        if (!activeUser.follower2_UserId.equals("null")) {
            System.out.print("," + activeUser.follower2_UserId + "\n");

        }
        System.out.println();
        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println();

        System.out.print("Followings Count : " + activeUser.followingCount + "\n");
        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.print("Followers Count : " + activeUser.followersCount + "\n");
        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println();

        System.out.print("Mobile Number : " + activeUser.mobileNumber + "\n");
        for (int i = 0; i <= 40; i++) {
            System.out.print("-");
        }

        System.out.println("\n\n");

    }

    // Messages And Notifications---------------------------------------------

    static void messages(Node profile, String file) throws IOException {

        for (;;) {

            BufferedWriter message = new BufferedWriter(new FileWriter(file, true));
            BufferedReader readMessages = new BufferedReader(new FileReader(file));

            System.out.println("\n1.Type\n2.View Chat\n3.Return\n");
            System.out.print("Enter Your Choice : ");
            switch (sc.nextInt()) {

                case 1:
                    sc.nextLine();
                    System.out.print("\nType Here: ");
                    String type = sc.nextLine();

                    message.write("[" + new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()) + "]  "
                            + activeUser.name + " : " + type);
                    message.newLine();
                    System.out.println("Message Sent.");
                    message.close();
                    break;

                case 2:
                    System.out.println();
                    String s = readMessages.readLine();
                    while (s != null) {
                        System.out.println(s.replace(activeUser.name, "You"));
                        s = readMessages.readLine();
                    }
                    readMessages.close();
                    break;
                case 3:
                    System.out.println();
                    return;
            }
        }
    }

    static void notifications() {
        // Add a notifications Column
        // Set Notification String when someone texts you.
    }
    // ---------------------------------------------------------------------------------------------------------------

}

// OBJECT TO STORE DATA OF USERS -----------------------------------------------

class Node {
    String userId, password, name;
    String following1_UserId;
    String following2_UserId;
    String follower1_UserId;
    String follower2_UserId;
    int followersCount;
    int followingCount;
    long mobileNumber;

    public Node(String userId, String password, String name, String following1_UserId, String following2_UserId,
            String follower1_UserId, String follower2_UserId, int followingCount, int followersCount,
            long mobileNumber) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.following1_UserId = following1_UserId;
        this.following2_UserId = following2_UserId;
        this.follower1_UserId = follower1_UserId;
        this.follower2_UserId = follower2_UserId;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.mobileNumber = mobileNumber;
    }
}

// THANK YOU -------------------------------------------------------------------