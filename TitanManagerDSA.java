import java.sql.*;
import java.util.*;

// ==========================================================
// 1. DATA MODELS & ADTs (List, Stack, Queue, Hashing)
// ==========================================================

// Custom Node for Singly Linked List (List ADT)
class EventNode {
    int id;
    String eventId, teamA, teamB, sport, status;
    int matchLevel; // Used for Priority/Sorting (1=Final, 4=Quarter)
    EventNode next;

    public EventNode(int id, String eid, String ta, String tb, String sp, String st, int lvl) {
        this.id = id; this.eventId = eid; this.teamA = ta; this.teamB = tb;
        this.sport = sp; this.status = st; this.matchLevel = lvl;
    }
}

// Node for Circularly Linked List (Sport Navigation Carousel)
class SportNode {
    String name;
    SportNode next;
    public SportNode(String name) { this.name = name; }
}

public class TitanManagerDSA {
    // Database Config
    private static final String URL = "jdbc:mysql://localhost:3306/TitanManagerDB";
    private static final String USER = "root", PASS = "zaqwsx";

    // DSA Implementation Variables
    private static EventNode head = null;                   // Singly Linked List Head
    private static Stack<String> navStack = new Stack<>();  // Stack ADT (Navigation History)
    private static SportNode currentSport;                  // Circular Linked List Pointer
    private static HashMap<String, EventNode> hashTable = new HashMap<>(); // Hashing O(1) Search

    private static Scanner sc = new Scanner(System.in);
    private static String currentUser = "";

    public static void main(String[] args) {
        initSportCarousel(); // Initialize Circularly Linked List
        if (checkDB()) runAuthMenu();
    }

    // ==========================================================
    // 2. CORE SYSTEM & DATABASE
    // ==========================================================

    private static void initSportCarousel() {
        // Implementing Circularly Linked List for the 4 games
        SportNode s1 = new SportNode("Basketball");
        SportNode s2 = new SportNode("Football");
        SportNode s3 = new SportNode("Table-tennis");
        SportNode s4 = new SportNode("Badminton");
        s1.next = s2; s2.next = s3; s3.next = s4; s4.next = s1; // Circular Link
        currentSport = s1;
    }

    private static boolean checkDB() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println(">> Database Status: ACTIVE (Connection: O(1))");
            return true;
        } catch (SQLException e) {
            System.err.println(">> Database Error: " + e.getMessage());
            return false;
        }
    }

    private static void refreshData() {
        head = null;
        hashTable.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM event");
            while (rs.next()) {
                EventNode n = new EventNode(rs.getInt("id"), rs.getString("eventId"), rs.getString("teamA"),
                        rs.getString("teamB"), rs.getString("sport"), rs.getString("status"), rs.getInt("matchLevel"));
                // List ADT: Insert at Head
                n.next = head; head = n;
                // Hashing: Store in Map for O(1) Lookup
                hashTable.put(n.eventId, n);
            }
        } catch (Exception e) {}
    }

    // ==========================================================
    // 3. PHASE 1: AUTHENTICATION (Login/Signup)
    // ==========================================================

    private static void runAuthMenu() {
        while (true) {
            System.out.println("\n========== TITAN MANAGER GATEWAY ==========");
            System.out.println("1. Login\n2. Signup\n3. Exit");
            System.out.print("Choice: ");
            String c = sc.nextLine();
            switch (c) {
                case "1" -> { if(handleLogin()) runAiDashboard(); }
                case "2" -> handleSignup();
                case "3" -> System.exit(0);
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static boolean handleLogin() {
        System.out.print("Email: "); String em = sc.nextLine();
        System.out.print("Password: "); String pw = sc.nextLine();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = conn.prepareStatement("SELECT firstName FROM Login WHERE email=? AND password=?");
            ps.setString(1, em); ps.setString(2, pw);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentUser = rs.getString("firstName");
                System.out.println("Login Success! Welcome " + currentUser);
                return true;
            }
        } catch (Exception e) {}
        System.out.println("Invalid Credentials.");
        return false;
    }

    private static void handleSignup() {
        System.out.print("First Name: "); String fn = sc.nextLine();
        System.out.print("Email: "); String em = sc.nextLine();
        System.out.print("Password: "); String pw = sc.nextLine();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Login (firstName, email, password) VALUES (?,?,?)");
            ps.setString(1, fn); ps.setString(2, em); ps.setString(3, pw);
            ps.executeUpdate();
            System.out.println("User Registered successfully.");
        } catch (Exception e) {}
    }

    // ==========================================================
    // 4. PHASE 2: AI PAGE & SPORT HUBS (Nested Switches)
    // ==========================================================

    private static void runAiDashboard() {
        navStack.push("Ai Dashboard");
        boolean session = true;
        while (session) {
            System.out.println("\n--- AI PAGE DASHBOARD (Logged: " + currentUser + ") ---");
            System.out.println("1. Basketball      2. Football");
            System.out.println("3. Table-Tennis    4. Badminton");
            System.out.println("5. Create Tournament (Update Events)");
            System.out.println("6. Show All Events (QuickSort/Search)");
            System.out.println("7. More (About/Profile)");
            System.out.println("8. LogOut");
            System.out.print("Action: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> viewSportHub("Basketball");
                case "2" -> viewSportHub("Football");
                case "3" -> viewSportHub("Table-tennis");
                case "4" -> viewSportHub("Badminton");
                case "5" -> createTournament();
                case "6" -> eventAnalytics();
                case "7" -> showMore();
                case "8" -> { session = false; navStack.pop(); }
                default -> System.out.println("Invalid Menu Option.");
            }
        }
    }

    private static void viewSportHub(String sportName) {
        navStack.push(sportName + " Hub");
        System.out.println("\n[" + sportName.toUpperCase() + " HUB]");
        System.out.println("1. Linear Search Match (O(N))\n2. Update Scores\n3. Back");
        String c = sc.nextLine();
        if (c.equals("1")) linearSearch(sportName);
        else if (c.equals("2")) updateScore(sportName);
        navStack.pop();
    }

    // ==========================================================
    // 5. DSA IMPLEMENTATIONS (Sort, Search, Hash)
    // ==========================================================

    private static void linearSearch(String sport) {
        refreshData();
        System.out.println("Searching List ADT...");
        EventNode temp = head;
        boolean found = false;
        while (temp != null) {
            if (temp.sport.equalsIgnoreCase(sport)) {
                System.out.println("ID: " + temp.eventId + " | " + temp.teamA + " vs " + temp.teamB);
                found = true;
            }
            temp = temp.next;
        }
        if (!found) System.out.println("No matches found.");
    }

    private static void eventAnalytics() {
        refreshData();
        System.out.println("\n--- Analytics Module ---");
        System.out.println("1. Hashing Lookup (O(1))\n2. QuickSort Rank (O(N log N))");
        String c = sc.nextLine();
        if (c.equals("1")) {
            System.out.print("Enter EventID: ");
            String key = sc.nextLine();
            EventNode res = hashTable.get(key); // Hashing
            System.out.println(res != null ? "Found: " + res.teamA + " vs " + res.teamB : "Not Found.");
        } else {
            // Convert Linked List to Array for QuickSort
            List<EventNode> list = new ArrayList<>();
            EventNode t = head;
            while(t != null) { list.add(t); t = t.next; }
            EventNode[] arr = list.toArray(new EventNode[0]);
            quickSort(arr, 0, arr.length - 1);
            System.out.println("Matches Ranked by Level (QuickSort):");
            for(EventNode e : arr) System.out.println("Level " + e.matchLevel + ": " + e.teamA + " vs " + e.teamB);
        }
    }

    private static void quickSort(EventNode[] arr, int l, int h) {
        if (l < h) {
            int p = partition(arr, l, h);
            quickSort(arr, l, p - 1);
            quickSort(arr, p + 1, h);
        }
    }

    private static int partition(EventNode[] arr, int l, int h) {
        int pivot = arr[h].matchLevel;
        int i = l - 1;
        for (int j = l; j < h; j++) {
            if (arr[j].matchLevel <= pivot) {
                i++;
                EventNode temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        EventNode temp = arr[i+1]; arr[i+1] = arr[h]; arr[h] = temp;
        return i + 1;
    }

    // ==========================================================
    // 6. DB UPDATES & METADATA
    // ==========================================================

    private static void createTournament() {
        System.out.print("EventID: "); String eid = sc.nextLine();
        System.out.print("Sport (Basketball/Football/Table-tennis/Badminton): "); String sp = sc.nextLine();
        System.out.print("Team A: "); String ta = sc.nextLine();
        System.out.print("Team B: "); String tb = sc.nextLine();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO event (eventId, teamA, teamB, sport, matchLevel, status) VALUES (?,?,?,?,4,'Upcoming')");
            ps.setString(1, eid); ps.setString(2, ta); ps.setString(3, tb); ps.setString(4, sp);
            ps.executeUpdate();
            System.out.println("New Tournament Event Created.");
        } catch (Exception e) {}
    }

    private static void updateScore(String sport) {
        System.out.print("Enter Match ID: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Score Team A: "); int sA = Integer.parseInt(sc.nextLine());
        System.out.print("Score Team B: "); int sB = Integer.parseInt(sc.nextLine());
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = conn.prepareStatement("UPDATE event SET scoreA=?, scoreB=?, status='Completed' WHERE id=?");
            ps.setInt(1, sA); ps.setInt(2, sB); ps.setInt(3, id);
            ps.executeUpdate();
            System.out.println("Bracket Progression Complete.");
        } catch (Exception e) {}
    }

    private static void showMore() {
        System.out.println("\n--- About App ---");
        System.out.println("Titan Manager: A Java Terminal-Based Sports Engine with MySQL Integration.");
        System.out.println("--- Developer Profile ---");
        System.out.println("Aditya Putta | 2520040061 | ECE Sec-8");
        System.out.println("Stack Status: " + navStack);
    }
}