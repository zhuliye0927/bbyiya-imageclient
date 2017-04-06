package com.bbyiya.service;

import javax.swing.JProgressBar;
import com.bbyiya.vo.SearchOrderParam;


public interface IOrderMgtService {
	String pbsdownloadImg(SearchOrderParam param,String fileDir,JProgressBar progressbar,String ticket);
	
	//public String login(String name,String password);
}
