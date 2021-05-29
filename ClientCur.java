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

    public static Storage getLargest(ArrayList<Storage> ServerInfo){
        Storage curLargest = new Storage();
        if(ServerInfo.size() == 1){
            curLargest = ServerInfo.get(0);
        }
        for(int i = 0;i<ServerInfo.size();i++){
            Storage cur = ServerInfo.get(i);
            for(int j = i;j<ServerInfo.size();j++){
                Storage cur2 = ServerInfo.get(j);
                if(cur2.core > cur.core){
                    curLargest = cur2;
                    break;
                }
            }
        }
        return curLargest;
    }

    public static void allToLargest(Storage LargestServer, String JobID, PrintWriter pw){
        pw.println(SCHD + " " + JobID + " " + LargestServer.ID + " " + LargestServer.type);
        pw.flush();
    }

    public static String CurJobID(String s){
        String [] JobInfo;
        String JobID;
        JobInfo = s.split("\\s+");
        JobID = JobInfo[2];
        System.out.println(JobID);
        return JobID;
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
                // System.out.println("Server : " + strCur);
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
        // Job = str;
        // System.out.println("server : " + str);

        // pw.println(GET);
        // pw.flush();

        // while(!str.equals(DOT)){
        //     str = bf.readLine();
        //     System.out.println("Server : " + str);
        //     pw.println(OK);
        //     pw.flush();
        //     if(!str.equals(DOT)&&!str.contains(DATA)){
        //         Servers.add(str);
        //     }
        // }

        // ServerInfo = Separate(Servers);

        // LargestServer = getLargest(ServerInfo);

        // str = Job;
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
               // System.out.println("Entry");
                // JobID = CurJobID(str);
                ScheduleJob(bf, pw, str);
               // System.out.println("EXIT");
                // allToLargest(LargestServer,JobID, pw);
               // str = bf.readLine();
               // System.out.println("server : " + str);
            }
            else if(str.contains(JOBP)){
              //  System.out.println("Entry2");
                ScheduleJob(bf, pw, str);
               // System.out.println("EXIT2");
               // str = bf.readLine();
               // System.out.println("server : " + str);
            }
            if(str.equals(NONE)){
                pw.flush();
                break;
            }
            str = bf.readLine();
           // System.out.println(str);
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
