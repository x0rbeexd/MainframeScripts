import java.io.*;
import java.net.*;

public class RemoteCommandAPIClient {
    public static void main(String[] args) {
        if (args.length < 5) {
            showHelp();
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        String password = args[3];
        String command = args[4];

        try {
            executeCommand(host, port, username, password, command);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void executeCommand(String host, int port, String user, String pass, String command) throws IOException {
        Socket socket = new Socket(host, port);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        // XMLSERVICE request format
        String xmlRequest = "<?xml version='1.0'?>"
                + "<xmlservice>"
                + "<auth><user>" + user + "</user><pass>" + pass + "</pass></auth>"
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

        String resp = response.toString();
        if (resp.contains("success")) {
            System.out.println("Command executed successfully!");
        } else {
            System.out.println("Command failed! Response: " + resp);
        }

        socket.close();
    }

    private static void showHelp() {
        System.out.println("Usage: java RemoteCommandAPIClient <host> <port> <username> <password> <command>");
        System.out.println("Example:");
        System.out.println("java RemoteCommandAPIClient 192.168.1.100 8475 myuser mypass DSPLIBL");
    }
}
