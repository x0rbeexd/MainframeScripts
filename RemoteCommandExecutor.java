import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RemoteCommandExecutor {
    public static void main(String[] args) {
        if (args.length < 3) {
            showHelp();
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String command = args[2];

        try {
            executeCommand(host, port, command);
        } catch (IOException e) {
            System.out.println("Authentication failed or connection error.");
            promptForCredentials(host, port, command);
        }
    }

    private static void executeCommand(String host, int port, String command) throws IOException {
        Socket socket = new Socket(host, port);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        // Example XML request (adjust based on IBM i API)
        String xmlRequest = "<?xml version='1.0'?>"
                + "<xmlservice>"
                + "<cmd>" + command + "</cmd>"
                + "</xmlservice>";

        out.write(xmlRequest.getBytes());
        out.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        if (response.toString().contains("success")) {
            System.out.println("Command executed successfully!");
        } else {
            System.out.println("Command failed!");
        }

        socket.close();
    }

    private static void promptForCredentials(String host, int port, String command) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            Socket socket = new Socket(host, port);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // XML request with credentials
            String xmlRequest = "<?xml version='1.0'?>"
                    + "<xmlservice>"
                    + "<auth><user>" + username + "</user><pass>" + password + "</pass></auth>"
                    + "<cmd>" + command + "</cmd>"
                    + "</xmlservice>";

            out.write(xmlRequest.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            if (response.toString().contains("success")) {
                System.out.println("Command executed successfully with authentication!");
            } else {
                System.out.println("Command failed even after authentication.");
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Error during authenticated execution: " + e.getMessage());
        }
    }

    private static void showHelp() {
        System.out.println("Usage: java RemoteCommandExecutor <host> <port> <command>");
        System.out.println("Example: java RemoteCommandExecutor 192.168.1.100 8475 DSPLIBL");
        System.out.println("If authentication fails, you will be prompted for username and password.");
    }
}
