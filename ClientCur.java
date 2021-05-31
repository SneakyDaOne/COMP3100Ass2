import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    //Static strings used
    private static String HELO = "HELO";
    private static String AUTH = "AUTH";
    private static String REDY = "REDY";
    private static String NONE = "NONE";
    private static String QUIT = "QUIT";
    private static String OK = "OK";
    private static String SCHD = "SCHD";
    private static String JOBN = "JOBN";
    private static String JOBP = "JOBP";
    private static String JCPL = "JCPL";
    private static String DOT = ".";
    private static String DATA = "DATA";
    private static String GETC = "GETS Capable";;
    //Function to specify server job is to be scheduled to, requires arraylist of servers
    public static String [] ServerState(ArrayList<String> ServerInfo){
        int max = ServerInfo.size();
        //determine largest server based on arraylist size
        String large = ServerInfo.get(max-1);   
        String [] Largest = large.split("\\s+");
        //Searches through server list in reverse
        //Find largest server that can run job and has no running jobs or waiting jobs
        //Return that server
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
        
        //If all servers have running and waiting jobs, schedule to the server that has the least running or waiting jobs
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
        //Base condition, return largest server if all servers have equal running and waiting jobs
        return Largest;
    }
    
    //Scheduling function, requires reader, printer and a string containing current job
    public static void ScheduleJob(BufferedReader bf, PrintWriter pw, String S)throws IOException, SocketException{
        //Presetting veriables to be utilized
        String [] JobInfo;
        String [] ServerData;
        String strCur;
        ArrayList<String> ServerInfo = new ArrayList<String>();
        ArrayList<Storage> FirstSer = new ArrayList<Storage>();
        //Splitting string containing job details into usable parts
        JobInfo = S.split("\\s+");
        //Calls gets capable to check for servers that can run job
        pw.println(GETC + " " + JobInfo[4] + " " + JobInfo[5] + " " + JobInfo[6]);
        pw.flush();
        strCur = bf.readLine();
        //Gets DATA string in order to determine number of servers returned
        ServerData = strCur.split("\\s+");
        Integer Servers = Integer.parseInt(ServerData[1]);
        pw.println(OK);
        pw.flush();
        //Loops as many times as server number
        for(int i = 0;i<Servers;i++){
            strCur = bf.readLine();
            //Dot checker and adds servers into arraylist of string
            if(!strCur.equals(DOT)){
                ServerInfo.add(strCur);
            }
        }
        pw.println(OK);
        pw.flush();
        //Sends arraylist of servers to ServerState function
        String [] temp = ServerState(ServerInfo);
        //Schedules job based on returned server
        pw.println(SCHD + " " + JobInfo[2] + " " + temp[0] + " " + temp[1]);
        pw.flush();
    }

    public static void main(String[] args) throws IOException, SocketException{
        //Connect to socket and create printer and reader
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        String str = "";
        //Determine machine name
        String name = System.getProperty("user.name");
        //Handshake protocol
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
        //Check condition of string for none
        while(!str.contains(NONE)){
            //Checks string for specificed key phrases as down below
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
