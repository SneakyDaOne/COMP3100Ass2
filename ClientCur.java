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
    
    public static String [] ServerState(ArrayList<String> ServerInfo){
        int max = ServerInfo.size();
        String large = ServerInfo.get(max-1);   
        String [] Largest = large.split("\\s+");
        for(int i = ServerInfo.size()-1; i>0;i--){
            String [] cur = ServerInfo.get(i).split("\\s+");
            Integer running = Integer.parseInt(cur[8]);
            Integer waiting = Integer.parseInt(cur[7]);
            System.out.println(cur[2]);
            if(running == 0){
                return cur;
            }
            if(waiting == 0){
                return cur;
            }
        }
        for(int i = ServerInfo.size()-2;i> 0;i--){
            System.out.println("Enter");
            String [] cur = ServerInfo.get(i).split("\\s+");
            int running1 = Integer.parseInt(cur[8]);
            int waiting1 = Integer.parseInt(cur[7]);
            if(running1 < Integer.parseInt(Largest[8])){
                Largest = cur;
            }
            if(waiting1 < Integer.parseInt(Largest[7])){
                Largest = cur;
            }
        }
        return Largest;
    }

    public static void ScheduleJob(BufferedReader bf, PrintWriter pw, String S)throws IOException, SocketException{
            String [] JobInfo;
            String [] ServerData;
            String strCur;
            ArrayList<String> ServerInfo = new ArrayList<String>();
            ArrayList<Storage> FirstSer = new ArrayList<Storage>();
            JobInfo = S.split("\\s+");
            pw.println(GETC + " " + JobInfo[4] + " " + JobInfo[5] + " " + JobInfo[6]);
            pw.flush();
            strCur = bf.readLine();
            ServerData = strCur.split("\\s+");
            Integer Servers = Integer.parseInt(ServerData[1]);
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
            String [] temp = ServerState(ServerInfo);
            pw.println(SCHD + " " + JobInfo[2] + " " + temp[0] + " " + temp[1]);
            pw.flush();
    }

    public static void main(String[] args) throws IOException, SocketException{
        
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        String str = "";
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
