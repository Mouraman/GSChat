import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


class Servidor extends Thread {
    private static ArrayList<BufferedWriter> clients;
    private static ServerSocket server;
    private String name;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;


    public Servidor(Socket con){
        this.con = con;
        try {
            in  = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        try{

            String msg;
            OutputStream ou =  this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clients.add(bfw);
            name = msg = bfr.readLine();

            while(!"OUT".equalsIgnoreCase(msg) && msg != null)
            {
                msg = bfr.readLine();
                sendToAll(bfw, msg);
                System.out.println(msg);
            }

        }catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void sendToAll(BufferedWriter bwExit, String msg) throws  IOException
    {
        BufferedWriter bwS;

        for(BufferedWriter bw : clients){
            bwS = (BufferedWriter)bw;
            if(!(bwExit == bwS)){
                bw.write(name + " -> " + msg+"\r\n");
                bw.flush();
            }
        }
    }
    public static void main(String []args) {

        try{
            //Cria os objetos necessário para instânciar o servidor
            JLabel lblMessage = new JLabel("Servidor Port:");
            JTextField txtPorta = new JTextField("12345");
            Object[] texts = {lblMessage, txtPorta };
            JOptionPane.showMessageDialog(null, texts);
            server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clients = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null,"Servidor active on Port: "+
                    txtPorta.getText());

            while(true){
                System.out.println("Waiting Conexion...");
                Socket con = server.accept();
                System.out.println("Client Connected...");
                Thread t = new Servidor(con);
                t.start();
            }

        }catch (Exception e) {

            e.printStackTrace();
        }
    }// Fim do método main
} //Fim da classe

