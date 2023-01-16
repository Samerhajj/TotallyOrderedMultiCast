package il.ac.kinneret.mjmay.common;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

public class Common {
    /**
     * Shows the list of available IP addresses and lets the user choose from them.
     * @return The chosen IP address.  If there's an error getting them, it will return 127.0.0.1 (local loopback)
     */
    public static InetAddress selectIPAddress()
    {
        // make a list of addresses to choose from
        // add in the usual ones
        Vector<InetAddress> adds = new Vector<InetAddress>();
        try {
            adds.add(InetAddress.getByAddress(new byte[] {0, 0, 0, 0}));
            adds.addElement(InetAddress.getLoopbackAddress());
        } catch (UnknownHostException ex) {
            // something is really weird - this should never fail
            System.out.println("Can't find IP address 0.0.0.0: " + ex.getMessage());
            ex.printStackTrace();
            // just return the loopback one
            return InetAddress.getLoopbackAddress();
        }

        try {
            // get the local IP addresses from the network interface listing
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while ( interfaces.hasMoreElements() )
            {
                NetworkInterface ni = interfaces.nextElement();
                // see if it has an IPv4 address
                Enumeration<InetAddress> addresses =  ni.getInetAddresses();
                while ( addresses.hasMoreElements())
                {
                    // go over the addresses and add them
                    InetAddress add = addresses.nextElement();
                    if (!add.isLoopbackAddress() && add.getAddress().length ==4)
                    {
                        adds.addElement(add);
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            // can't get local addresses, something's wrong
            System.out.println("Can't get network interface information: " + ex.getLocalizedMessage());
            // just return the loopback address
            return InetAddress.getLoopbackAddress();
        }

        System.out.println("Choose an IP address to listen on :");
        for (int i = 0; i < adds.size(); i++)
        {
            // show it in the list
            System.out.println(i + ": " + adds.elementAt(i).toString());
        }

        BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
        int choice = -1;

        while ( choice < 0 || choice >= adds.size())
        {
            System.out.print(": ");
            try {
                String line = brIn.readLine();
                choice = Integer.parseInt(line.trim());
            }
            catch (Exception ex) {
                System.out.print("Error parsing choice\n: ");
            }
        }
        // we have a choice
        return adds.elementAt(choice);
    }

    /**
     * Parses the neighbors file and loads up the list into a Vector
     * @param fileName The neighbors file
     * @return A Vector with the neighbors loaded up as Strings
     */
    public static Vector<String> parseNeighbors (String fileName)
    {
        Vector<String> neighbors = new Vector<>();
        BufferedReader brIn = null;
        try {
            // open the file for reading
            brIn = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            // read it in line by line
            String line;
            while ( (line = brIn.readLine()) != null)
            {
                // see if the line has an IP address and port in it
                String[] parts = line.split(":");
                if ( parts.length >= 2)
                {
                    try {
                        // try to make an IP address and port out of the parts
                        InetAddress add = InetAddress.getByName(parts[0].trim());
                        int port = Integer.parseInt(parts[1].trim());
                        neighbors.addElement(add.toString().substring(1) + ":" + Integer.toString(port));
                    }
                    catch (UnknownHostException | NumberFormatException | SecurityException ex)
                    {
                        // something went wrong with the parsing and this isn't an address
                        System.out.println("Error parsing address: " + parts[0] + " with port " + parts[1]);
                    }
                }
            }
            // done reading the file, we have the neighbors
            brIn.close();

        } catch (FileNotFoundException e) {
            // can't get the file of neighbors
            System.out.println("Can't find neighbors file: " + fileName + ": " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading neighbors file: " + fileName + ": " + e.getMessage());
        }
        finally
        {
            // close the neighbors file
            if (brIn != null) { try { brIn.close();} catch (Exception ex) {} }
        }
        System.out.println("Loaded " + neighbors.size() + " neighbors");
        return neighbors;
    }
}
