package com.bbyiya.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.plaf.metal.MetalProgressBarUI;

import com.bbyiya.service.IOrderMgtService;
import com.bbyiya.service.impl.OrderMgtServiceImpl;
import com.bbyiya.vo.SearchOrderParam;

public class initMainPanel extends JFrame{
	private static final long serialVersionUID=1L;
	Timer timer; 
	JProgressBar progressbar;
	
	String ticket="";
	
	public initMainPanel(String ticket){
		setTitle("下载订单图片");//同super("登录窗口")
		this.ticket=ticket;
		this.setSize(500, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		/* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();    
        // 添加面板
        this.add(panel);
        /* 
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        this.setVisible(true);
        //GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
       // GraphicsDevice gd=ge.getDefaultScreenDevice();
        //gd.setFullScreenWindow(this);
        
	}
	
	
	private  void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);
        // 创建 JLabel
        JLabel orderIdLabel = new JLabel("订单号:");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        orderIdLabel.setBounds(20,20,80,25);
        panel.add(orderIdLabel);

        /* 
         * 创建文本域用于订单号输入
         */
        JTextField orderText = new JTextField(20);
        orderText.setBounds(100,20,165,25);
        panel.add(orderText);
        
        //订单状态下拉框
        JLabel statusLabel = new JLabel("订单状态:");
        statusLabel.setBounds(20,50,80,25);
        panel.add(statusLabel);
        
        String[] statuslist={"等待发货","已支付","已发货","已签收","等待支付"};
        JComboBox<String> statusbox=new JComboBox<String>(statuslist);
        statusbox.setBounds(100,50,165,25);
        panel.add(statusbox);
        
        
        //日期
        JLabel dateLabel1 = new JLabel("开始日期:");
        dateLabel1.setBounds(20,80,80,25);
        panel.add(dateLabel1);
        
        //日期
        JLabel dateLabel2 = new JLabel("结束日期:");
        dateLabel2.setBounds(20,110,80,25);
        panel.add(dateLabel2);
        
        JTextField startTime = new JTextField();
		JTextField endTime = new JTextField();
		startTime.setBounds(100, 80, 165,25);
		endTime.setBounds(100, 110, 165,25);


        // 定义日历控件面板类
 		CalendarPanel p1 = new CalendarPanel(startTime, "yyyy-MM-dd");
 		p1.initCalendarPanel();
 		CalendarPanel p2 = new CalendarPanel(endTime, "yyyy-MM-dd");
 		p2.initCalendarPanel();
 		
 		panel.add(p1);
 		panel.add(p2);
 		panel.add(startTime);
 		panel.add(endTime);
	
 		//下载的目录
        JLabel fileDirLabel2 = new JLabel("下载目录:");
        fileDirLabel2.setBounds(20,140,80,25);
        panel.add(fileDirLabel2);
 		
        JTextField fileDirText = new JTextField(20);
        fileDirText.setBounds(100,140,165,25);
        panel.add(fileDirText);
        
        
        //提示的lable
        JLabel tipLabel = new JLabel("提示：如果下载目录为空，默认的下载目录为：D:\\orderImgs\\");
        tipLabel.setBounds(20,170,400,25);
        panel.add(tipLabel);
        
        
        panel.add(fileDirLabel2);  
 		
 		
        

        // 创建登录按钮
        JButton searchButton = new JButton("下载");
        searchButton.setBounds(100, 200, 80, 25);
        panel.add(searchButton);
        
        JLabel label = new JLabel("点击运行按钮开始", JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        
        //进度条
        progressbar= new JProgressBar(0,100);
        progressbar.setOrientation(JProgressBar.HORIZONTAL);
        progressbar.setSize(200, 30);
        progressbar.setBounds(100, 230, 250, 30);
        progressbar.setMinimum(0);
        progressbar.setMaximum(100);
        progressbar.setValue(0);
        progressbar.setStringPainted(true);
        //progressbar.setPreferredSize(new Dimension(300, 20));
        progressbar.setBorderPainted(true);
        progressbar.setBackground(Color.green);
        progressbar.setForeground(Color.BLUE);
        //progressbar.setIndeterminate(true);
        //progressbar.setBackground(Color.pink);
        progressbar.setVisible(false);
        panel.add(progressbar, BorderLayout.SOUTH);
        progressbar.setUI(new MetalProgressBarUI());

        searchButton.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						searchButton.setEnabled(false);
		        		progressbar.setVisible(true);
		        		progressbar.setValue(0); 
		        		String orderId=orderText.getText();
		        		String status=statusbox.getSelectedItem().toString();
		        		Integer sta=null;
		        		if(status!=null&&status.equals("已支付")){
		        			sta=1;
		        		}else if(status!=null&&status.equals("等待发货")){
		        			sta=2;
		        		}else if(status!=null&&status.equals("已发货")){
		        			sta=3;
		        		}else if(status!=null&&status.equals("已签收")){
		        			sta=4;
		        		}else if(status!=null&&status.equals("等待支付")){
		        			sta=0;
		        		}else{
		        			sta=null;
		        		}
		        		progressbar.setValue(1);
		        	
		        		String startDate=startTime.getText();
		        		String endDate=endTime.getText();
		        		String fileDir=fileDirText.getText();
		        		SearchOrderParam param=new SearchOrderParam();
		        		param.setOrderNo(orderId);
		        		param.setStatus(sta);
		        		param.setStartTimeStr(startDate);
		        		param.setEndTimeStr(endDate);
		        		downloadImage(param,fileDir,panel,progressbar,ticket);
		        		searchButton.setEnabled(true);
		        		
						
					}}).start();
        		
        	}
        });
    }
	
	
    
	 public  void downloadImage(SearchOrderParam param,String fileDir,JPanel panel,JProgressBar progressbar,String ticket){	
	    	IOrderMgtService service=new OrderMgtServiceImpl();
	    	String result=service.pbsdownloadImg(param, fileDir,progressbar,ticket);
	    	progressbar.setValue(100);
	    	if(result!=null&&result.equals("-1")){
	    		JOptionPane.showMessageDialog(panel,"下载目录不存在！","提示",JOptionPane.WARNING_MESSAGE);
	    		return;
	    	}else{
	    		JOptionPane.showMessageDialog(panel,"下载成功！","提示",JOptionPane.WARNING_MESSAGE);
	    		return;
	    	}
	    	
	}
	 
	
}
