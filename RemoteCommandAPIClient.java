import java.io.*;
import java.net.*;
import java.util.Base64;

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
        String urlString = "http://" + host + ":" + port + "/qxml"; // XMLSERVICE endpoint
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml");

        // Basic Auth header
        String auth = user + ":" + pass;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

        // XML payload
        String xmlRequest = "<?xml version='1.0'?>"
                + "<xmlservice>"
                + "<cmd>" + command + "</cmd>"
                + "</xmlservice>";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(xmlRequest.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String resp = response.toString();
        if (resp.contains("success") || responseCode == 200) {
            System.out.println("Command executed successfully!");
        } else {
            System.out.println(" Command failed! Response: " + resp);
        }
    }

    private static void showHelp() {
        System.out.println("Usage: java RemoteCommandAPIClient <host> <port> <username> <password> <command>");
        System.out.println("Example:");
        System.out.println("java RemoteCommandAPIClient 192.168.1.100 8475 myuser mypass DSPLIBL");
    }
}
