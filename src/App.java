import java.sql.*;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employeedb?useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "Jawan_07"
            );

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n1. Add Employee");
                System.out.println("2. View All Employees");
                System.out.println("3. Search Employee by ID");
                System.out.println("4. Update Employee Details");
                System.out.println("5. Deactivate Employee");
                System.out.println("6. View by Department");
                System.out.println("7. Salary Report");
                System.out.println("8. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();

                if (choice == 1) {
                    System.out.print("EMP_ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("EMP_NAME: ");
                    String name = sc.nextLine();
                    System.out.print("DEPARTMENT: ");
                    String dept = sc.nextLine();
                    System.out.print("SALARY: ");
                    int salary = sc.nextInt();
                    System.out.print("CONTACT: ");
                    long contact = sc.nextLong();
                    sc.nextLine();
                    System.out.print("EMAIL: ");
                    String email = sc.nextLine();

                    String sql = "INSERT INTO EMPLOYEE VALUES (?, ?, ?, ?, ?, ?, TRUE)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.setString(2, name);
                    ps.setString(3, dept);
                    ps.setInt(4, salary);
                    ps.setLong(5, contact);
                    ps.setString(6, email);
                    ps.executeUpdate();

                    System.out.println("Employee Added");
                }

                else if (choice == 2) {
                    String sql = "SELECT * FROM EMPLOYEE WHERE STATUS = TRUE";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        System.out.println(
                            rs.getInt("EMP_ID") + " | " +
                            rs.getString("EMP_NAME") + " | " +
                            rs.getString("DEPARTMENT") + " | " +
                            rs.getInt("SALARY") + " | " +
                            rs.getString("EMAIL")
                        );
                    }
                }

                else if (choice == 3) {
                    System.out.print("Enter EMP_ID: ");
                    int id = sc.nextInt();

                    String sql = "SELECT * FROM EMPLOYEE WHERE EMP_ID = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        System.out.println(
                            rs.getString("EMP_NAME") + " | " +
                            rs.getString("DEPARTMENT") + " | " +
                            rs.getInt("SALARY")
                        );
                    } else {
                        System.out.println("Employee Not Found");
                    }
                }

                else if (choice == 4) {
                    System.out.print("EMP_ID: ");
                    int id = sc.nextInt();
                    System.out.print("New Salary: ");
                    int salary = sc.nextInt();

                    String sql = "UPDATE EMPLOYEE SET SALARY = ? WHERE EMP_ID = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, salary);
                    ps.setInt(2, id);
                    ps.executeUpdate();

                    System.out.println("Employee Updated");
                }

                else if (choice == 5) {
                    System.out.print("EMP_ID: ");
                    int id = sc.nextInt();

                    String sql = "UPDATE EMPLOYEE SET STATUS = FALSE WHERE EMP_ID = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.executeUpdate();

                    System.out.println("Employee Deactivated");
                }

                else if (choice == 6) {
                    sc.nextLine();
                    System.out.print("Department: ");
                    String dept = sc.nextLine();

                    String sql = "SELECT EMP_NAME, SALARY FROM EMPLOYEE WHERE DEPARTMENT = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, dept);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        System.out.println(
                            rs.getString("EMP_NAME") + " | " +
                            rs.getInt("SALARY")
                        );
                    }
                }

                else if (choice == 7) {
                    String sql = "SELECT MIN(SALARY), MAX(SALARY), AVG(SALARY) FROM EMPLOYEE";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        System.out.println(
                            "Min: " + rs.getInt(1) +
                            " Max: " + rs.getInt(2) +
                            " Avg: " + rs.getDouble(3)
                        );
                    }
                }

                else if (choice == 8) {
                    con.close();
                    sc.close();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
