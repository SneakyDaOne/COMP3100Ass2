import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private static String HELO = "HELO";
    private static String AUTH = "AUTH";
    private static String REDY = "REDY";
    private static String NONE = "NONE";
    private static String QUIT = "QUIT";
    private static String GET = "GETS All";
    private static String OK = "OK";
    private static String SCHD = "SCHD";
    private static String JOBN = "JOBN";
    private static String JOBP = "JOBP";
    private static String JCPL = "JCPL";
    private static String DOT = ".";
    private static String DATA = "DATA";
    private static String ERR = "ERR";
    private static String GETC = "GETS Capable";;

    public static ArrayList<Storage> Separate(ArrayList<String> Servers){
        String [] Info;
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        for (int i = 0; i<Servers.size(); i++){
            Storage cur = new Storage();
            Info = Servers.get(i).split("\\s+");
            cur.ID = Info[0];
            cur.type = Integer.parseInt(Info[1]);
            cur.core = Integer.parseInt(Info[4]);
            cur.memory = Integer.parseInt(Info[5]);
            cur.disk = Integer.parseInt(Info[6]);
            ServerInfo.add(cur);
        }
        return ServerInfo;
    }

    public static void ScheduleJob(BufferedReader bf, PrintWriter pw, String S)throws IOException, SocketException{
            String [] JobInfo;
            String [] ServerData;
            ArrayList<String> ServerInfo = new ArrayList<String>();
            String strCur;
            ArrayList<Storage> FirstSer = new ArrayList<Storage>();
            JobInfo = S.split("\\s+");
            pw.println(GETC + " " + JobInfo[4] + " " + JobInfo[5] + " " + JobInfo[6]);
            pw.flush();
            strCur = bf.readLine();
            ServerData = strCur.split("\\s+");
            Integer Servers = Integer.parseInt(ServerData[1]);
            System.out.println(strCur);
            pw.println(OK);
            pw.flush();
            for(int i = 0;i<Servers;i++){
                strCur = bf.readLine();
                if(!strCur.equals(DOT)){
                    ServerInfo.add(strCur);
                }
            }
            pw.println(OK);
            pw.flush();
            String [] temp = ServerInfo.get(1).split("\\s+");
            pw.println(SCHD + " " + JobInfo[2] + " " + temp[0] + " " + temp[1]);
            pw.flush();
    }

    public static void main(String[] args) throws IOException, SocketException{
        
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        String str = "";
        String Largest = "";
        String Job = "";
        String JobID = "";
        ArrayList<String> Servers = new ArrayList<String>();
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        Storage LargestServer = new Storage();
        String name = System.getProperty("user.name");

        pw.println(HELO);
        pw.flush();

        str = bf.readLine();
        System.out.println("server : " + str);

        pw.println(AUTH + " " + name);
        pw.flush();

        str = bf.readLine();
        System.out.println("server : " + str);

        pw.println(REDY);
        pw.flush();

        str = bf.readLine();
        System.out.println("server : " + str);
        System.out.println(str);
        while(!str.contains(NONE)){
            if(str.contains(JCPL)){
                pw.println(REDY);
                pw.flush();
            }
            else if(str.equals(OK)||str.contains(JCPL)){
                pw.println(REDY);
                pw.flush();
            }
            else if(str.contains(JOBN)){
                ScheduleJob(bf, pw, str);
            }
            else if(str.contains(JOBP)){
                ScheduleJob(bf, pw, str);
            }
            if(str.equals(NONE)){
                pw.flush();
                break;
            }
            str = bf.readLine();
            pw.flush();
        }
        pw.println(QUIT);
        pw.flush();
        str = bf.readLine();
        System.out.println("Server : " + str);
        in.close();
        pw.close();
        s.close();
    }
}
