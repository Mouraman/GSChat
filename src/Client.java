import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

class Client extends JFrame implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;
    private JTextArea text;
    private JTextField txtMsg;
    private JButton btnSend;
    private JButton btnSair;
    private JLabel lblHistorico;
    private JLabel lblMsg;
    private JPanel pnlContent;
    private Socket socket;
    private OutputStream ou;
    private Writer ouw;
    private BufferedWriter bfw;
    private JTextField txtIP;
    private JTextField txtPorta;
    private JTextField txtName;


    public Client() throws IOException {
        JLabel lblMessage = new JLabel("Verificar!");
        txtIP = new JTextField("127.0.0.1");
        txtPorta = new JTextField("12345");
        txtName = new JTextField("Client");
        Object[] texts = {lblMessage, txtIP, txtPorta, txtName};
        JOptionPane.showMessageDialog(null, texts);
        pnlContent = new JPanel();
        text = new JTextArea(10, 20);
        text.setEditable(false);
        text.setBackground(new Color(240, 240, 240));
        txtMsg = new JTextField(20);
        lblHistorico = new JLabel("Histórico");
        lblMsg = new JLabel("Mensagem");
        btnSend = new JButton("Enviar");
        btnSend.setToolTipText("Enviar Mensagem");
        btnSair = new JButton("Sair");
        btnSair.setToolTipText("Sair do Chat");
        btnSend.addActionListener(this);
        btnSair.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(text);
        text.setLineWrap(true);
        pnlContent.add(lblHistorico);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnSair);
        pnlContent.add(btnSend);
        pnlContent.setBackground(Color.LIGHT_GRAY);
        text.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        setTitle(txtName.getText());
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /***
     * Método usado para conectar no server socket, retorna IO Exception caso dê algum erro.
     * @throws IOException
     */
    public void conectar() throws IOException {

        socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
        ou = socket.getOutputStream();
        ouw = new OutputStreamWriter(ou);
        bfw = new BufferedWriter(ouw);
        bfw.write(txtName.getText() + "\r\n");
        bfw.flush();
    }

    /***
     * Método usado para enviar mensagem para o server socket
     * @param msg do tipo String
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void sendingMessage(String msg) throws IOException {

        if (msg.equals("Sair")) {
            bfw.write("Disconected \r\n");
            text.append("Disconected \r\n");
        } else {
            bfw.write(msg + "\r\n");
            text.append(txtName.getText() + " diz -> " + txtMsg.getText() + "\r\n");
        }
        bfw.flush();
        txtMsg.setText("");
    }

    /**
     * Método usado para receber mensagem do servidor
     *
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void listening() throws IOException {

        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while (!"Exit".equalsIgnoreCase(msg))

            if (bfr.ready()) {
                msg = bfr.readLine();
                if (msg.equals("Exit"))
                    text.append("Servidor went down! \r\n");
                else
                    text.append(msg + "\r\n");
            }
    }

    /***
     * Método usado quando o usuário clica em Exit
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void Exit() throws IOException {

        sendingMessage("Exit");
        bfw.close();
        ouw.close();
        ou.close();
        socket.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getActionCommand().equals(btnSend.getActionCommand()))
                sendingMessage(txtMsg.getText());
            else if (e.getActionCommand().equals(btnSair.getActionCommand()))
                Exit();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                sendingMessage(txtMsg.getText());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) throws IOException {

        Client app = new Client();
        app.conectar();
        app.listening();
    }
}
