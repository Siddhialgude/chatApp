import java.io.*;
import java.net.*;  
import javax.swing.*;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
class Server extends JFrame{

  Socket socket;
  BufferedReader br;
  PrintWriter out; 
  ServerSocket server;
  
  private JLabel heading = new JLabel("Server Area");
  private JLabel iconLabel = new JLabel();
  private JTextArea messageArea = new JTextArea();
  private JTextField messageInput = new JTextField();
  private Font font = new Font("Roboto", Font.PLAIN, 20);

  public Server(){
    try{
        server=new ServerSocket(8007);
        System.out.println("server is ready to accept connection ");
        System.out.println("waiting...");
        socket=server.accept();

        br=new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out=new PrintWriter(socket.getOutputStream());
        createGUI();
        startReading();
        startWriting();

    }
    catch(Exception e){
      e.printStackTrace();

    }
  }
  public void createGUI() {
    this.setTitle("Client Messenger");
    this.setSize(600, 700);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    heading.setFont(new Font("Arial", Font.BOLD, 30));
    messageArea.setFont(font);
    messageInput.setFont(font);
    messageInput.setBackground(new Color(255,216,106));

    try { 
        ImageIcon icon = new ImageIcon(ImageIO.read(new File("cicon2.png")));
        Image image = icon.getImage().getScaledInstance(60, 40, Image.SCALE_SMOOTH);
        iconLabel.setIcon(new ImageIcon(image));
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    heading.setVerticalTextPosition(SwingConstants.BOTTOM);
    heading.setHorizontalTextPosition(SwingConstants.CENTER);
    heading.setHorizontalAlignment(SwingConstants.CENTER);
    heading.setBorder(BorderFactory.createEmptyBorder(0,20,0,30));
    heading.setLayout(new BorderLayout()); 
    heading.setBackground(new Color(78,172,109)); 
    heading.add(iconLabel, BorderLayout.EAST);
    
    messageArea.setEditable(false);
    messageArea.setBackground(new Color(78,172,109));
    messageInput.setHorizontalAlignment(SwingConstants.CENTER);
    messageInput.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String contentToSend = messageInput.getText();
            messageArea.append("Me: " + contentToSend + "\n");
            out.println(contentToSend);
            out.flush();
            messageInput.setText("");
        }
    });

    JScrollPane jScrollPane = new JScrollPane(messageArea);
    jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    this.setLayout(new BorderLayout());
    this.add(heading, BorderLayout.NORTH);
    this.add(jScrollPane, BorderLayout.CENTER);
    this.add(messageInput, BorderLayout.SOUTH);

    this.setVisible(true);
}
  public void startReading()throws IOException {

    Runnable r1=()->{

      System.out.println("reader started");
      try
      {
        
      while(true){
        
            String msg=br.readLine();
          if(msg.equals("exit")){

            System.out.println("client terminated the chat");
            JOptionPane.showMessageDialog(this, "Client terminated the chat");
            messageInput.setEnabled(false);
            socket.close();
            this.dispose();
            break;
          }
          messageArea.append("Client : " + msg + "\n");
          //System.out.println("client : "+msg);
        
      }
    }catch(Exception e){
      this.dispose();
      System.out.println("connection closed");
     // e.printStackTrace();
    }


    };
    new Thread(r1).start();

  }
  public void startWriting(){
    Runnable r2=()->{

      System.out.println("Writer started...");
      try{

      while( ! socket.isClosed()){
        
          BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
          String content=br1.readLine();
          out.println(content);
          out.flush();
          if(content.equals("exit")){
            JOptionPane.showMessageDialog(this, "Client terminated the chat");
            socket.close();
            break;
          }
         

      }
      if(socket.isClosed()){
        this.dispose();
      }
      
    }catch(Exception e){
      // e.printStackTrace();
      this.dispose();
      System.out.println("connection closed");
    }

    };
    new Thread(r2).start();
  }
  public static void main(String[] args) {
    System.out.println("this is server going to start it");
    new Server();
  }
}