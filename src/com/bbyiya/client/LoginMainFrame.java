package com.bbyiya.client;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.*;

import net.sf.json.JSONObject;

import com.bbyiya.service.IOrderMgtService;
import com.bbyiya.service.impl.OrderMgtServiceImpl;
import com.bbyiya.utlis.HttpRequestHelper;
import com.bbyiya.vo.ReturnModel;
public class LoginMainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final static int HEIGHT=300;
	private final static int WIDTH=400;
	
	private String inUsername,inPassword;
	public LoginMainFrame(){
		setTitle("用户登录");//同super("登录窗口")
		this.setResizable(false);//也可以不用this,以后都不用
		
		SetupUI();
		MyWindowAdapter mwa=new MyWindowAdapter();
		addWindowListener(mwa);   //指的是super或者LoginMainFrame
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width/2-WIDTH/2, screenSize.height/2-HEIGHT/2);
		setSize(WIDTH, HEIGHT);
	}
	
	public void SetupUI(){

		Container container=getContentPane();   //JFrame.getContentPane();
		container.setLayout(new FlowLayout());
		Box hor1=Box.createHorizontalBox();
		Box hor2=Box.createHorizontalBox();
		Box hor3=Box.createHorizontalBox();
		Box hor4=Box.createHorizontalBox();
		Box ver1=Box.createVerticalBox();
		
		JLabel lblTitle=new JLabel("登陆窗口");
		lblTitle.setFont(new Font("",Font.BOLD,18));
		container.add(hor1);
		hor1.add(lblTitle);
		
		JLabel lblUserID=new JLabel("登录名：");
		final JTextField txtUserID=new JTextField();
		txtUserID.setPreferredSize(new Dimension(150,30));
		hor2.add(lblUserID);
		hor2.add(txtUserID);
		hor2.setSize(300, 80);
		
		JLabel lblPassword=new JLabel("密    码：");
		final JPasswordField txtPassword=new JPasswordField();
		txtPassword.setPreferredSize(new Dimension(150,30));
		hor3.add(lblPassword);
		hor3.add(txtPassword);
		hor3.setSize(300, 80);
		
		JButton btnSubmit=new JButton("确认");
		btnSubmit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				inUsername=txtUserID.getText();
				inPassword=String.valueOf(txtPassword.getPassword());
				LoginCheck(inUsername,inPassword);	
			}
		});
		JButton btnClear=new JButton("退出");
		btnClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txtUserID.setText("");
				txtPassword.setText("");	
				int i=JOptionPane.showConfirmDialog(null,"确认要退出吗？","确认",JOptionPane.YES_NO_OPTION);
				if(i==JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		hor4.add(btnSubmit);
		hor4.add(Box.createHorizontalStrut(15));
		hor4.add(btnClear);
		
		ver1.add(hor1);
		ver1.add(Box.createVerticalStrut(20));
		ver1.add(hor2);
		ver1.add(Box.createVerticalStrut(20));
		ver1.add(hor3);
		ver1.add(Box.createVerticalStrut(20));
		ver1.add(hor4);
		container.add(ver1);
	}
	public void LoginCheck(String UserID,String Password){
		if(UserID.equals("")||UserID==null){
			JOptionPane.showMessageDialog(null,"没有输入用户名。","登陆系统：提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		else if(Password.equals("")||Password==null){
			JOptionPane.showMessageDialog(null,"没有输入密码。","登陆系统：提示",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String urlString = "http://mpic.bbyiya.com/login/loginPhone";
		String dataString = "phone=" + UserID + "&pwd=" + Password ;
		String result= HttpRequestHelper.sendPost(urlString, dataString);
		
		JSONObject jsonobject=JSONObject.fromObject(result);
		String message="密码错误或用户名未注册！";
//		if(jsonobject!=null&&jsonobject.has("StatusReson")&&jsonobject.get("StatusReson")!=null){
//			message=jsonobject.get("StatusReson").toString();
//		}
		if(jsonobject!=null&&jsonobject.has("Statu")&&jsonobject.get("Statu")!=null&&jsonobject.get("Statu").equals("1")){
			//String ticket=jsonobject.get("BaseModle.ticket").toString();
			JSONObject baseobject=jsonobject.getJSONObject("BaseModle");
			
			String ticket=baseobject.get("ticket").toString();
			initMainPanel amf= new initMainPanel(ticket);
			amf.setVisible(true);
			this.dispose();  //用于登陆后关闭。
		}else{
			JOptionPane.showMessageDialog(null,message);
		}		
	}
	public static void main(String[] args) {
		LoginMainFrame lmf=new LoginMainFrame();
		lmf.setVisible(true);
	}

}
class MyWindowAdapter extends WindowAdapter
{
   public void windowClosing(WindowEvent e)//窗口正处在关闭过程中时调用
   {
       System.exit(0);
    }
   
}

