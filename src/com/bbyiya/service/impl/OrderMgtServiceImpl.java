package com.bbyiya.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JProgressBar;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;

import com.bbyiya.model.OOrderproductdetails;
import com.bbyiya.service.IOrderMgtService;
import com.bbyiya.utlis.DateUtil;
import com.bbyiya.utlis.FileDownloadUtils;
import com.bbyiya.utlis.FileUtils;
import com.bbyiya.utlis.HttpRequestHelper;
import com.bbyiya.utlis.ObjectUtil;
import com.bbyiya.utlis.RotateImage;
import com.bbyiya.vo.SearchOrderParam;

public class OrderMgtServiceImpl implements IOrderMgtService {
	public String pbsdownloadImg(SearchOrderParam param,String basePath,JProgressBar progressbar,String ticket){
		//设置结束时间为一天的最后的时间点
		param.setEndTimeStr(DateUtil.getEndTime(param.getEndTimeStr()));
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultDefaultValueProcessor() {
		    public Object getDefaultValue(Class type) {
		        return null;
		    }
		});
		
		JSONObject object = JSONObject.fromObject(param,jsonConfig);
		
		String urlString = "http://mpic.bbyiya.com/pbs/order/getOrderList";
		String dataString = "myproductJson="+object.toString()+"&index=0&size=0&ticket="+ticket ;
		String result= HttpRequestHelper.sendPost(urlString, dataString);
		
		JSONObject jsonobject=JSONObject.fromObject(result);
		JSONObject baseobject=jsonobject.getJSONObject("BaseModle");
		List<JSONObject> orderlist=(List<JSONObject>)baseobject.get("list");
		//List<OOrderproducts> orderlist=productDao.QueryBySearchFilter(param);
		
		int initpro=5;//初始进度
		progressbar.setValue(initpro);
		String sep=System.getProperty("file.separator");
		//String  basePath = System.getProperty("user.home") +sep + "imagedownloadtemp"+sep+"orderImg";
		if(basePath==null||basePath.equals("")){
			basePath="D:\\orderImgs\\";
		}
		try {
			FileUtils.isDirExists(basePath);
		} catch (Exception e) {
			basePath="D:\\orderImgs\\";
			FileUtils.isDirExists(basePath);
		}
		
		Calendar c1 =  Calendar.getInstance();
		Date nowtime=new Date();
		c1.setTime(nowtime); 
		String file_temp=DateUtil.getTimeStr(c1.getTime(), "yyyyMMddHHmm");
		//创建文件夹
		FileUtils.isDirExists(basePath+sep+file_temp);
		double totalpro=initpro;
		double spacepro=0.0;//间隔进度	
		
		
		for (JSONObject order2 : orderlist) {
			
			String buyeruserid=order2.get("buyeruserid").toString();
			String producttile=order2.get("producttitle").toString();
			String propertystr=order2.get("propertystr").toString();
			String count=order2.get("count").toString();
			String userorderid=order2.get("userorderid").toString();
			String orderproductid=order2.get("orderproductid").toString();
			String StyleId=order2.get("styleid").toString();
			
			FileUtils.isDirExists(basePath+sep+file_temp+sep+buyeruserid+"-"+producttile+"-"+propertystr.replaceAll("/", "-")+"×"+count+"("+userorderid+")");
			int i=1;		
			
			
			int type=Integer.parseInt(StyleId)%2;//判断产品类型
			//List<OOrderproductdetails> detallist=detailsDao.QueryByOrderProductId(orderproductid);
			//改用调接口的模式
			String urlString2 = "http://mpic.bbyiya.com/order/getOrderProductdetailsByOrderProductId";
			String dataString2 = "orderProductId="+orderproductid+"&ticket="+ticket ;
			String detailresult= HttpRequestHelper.sendPost(urlString2, dataString2);
			
			JSONObject  detailjsonobject=JSONObject.fromObject(detailresult);
			JSONObject detailbaseobject=detailjsonobject.getJSONObject("BaseModle");
			List<JSONObject> detallist=(List<JSONObject>)detailbaseobject.get("list");
			
			
			if(detallist!=null&&detallist.size()>0){
				spacepro=(double)90/(double)(detallist.size()*orderlist.size());
			}else{
				spacepro=(double)90/(double)orderlist.size();
				totalpro+=spacepro;
			}
			for (JSONObject jsondetail : detallist) {
				String imageurl=jsondetail.getString("imageurl");
				String backimageurl=jsondetail.getString("backimageurl");
				//OOrderproductdetails detail = (OOrderproductdetails)JSONObject.toBean(jsondetail,OOrderproductdetails.class);
				if(imageurl!=null&&!imageurl.equals("null")&&!imageurl.equals("")){
					imageurl="http://pic.bbyiya.com/"+imageurl;
					//.setImageurl("http://pic.bbyiya.com/"+detail.getImageurl());
				}
				if(backimageurl!=null&&!backimageurl.equals("null")&&!backimageurl.equals("")){
					
					backimageurl="http://pic.bbyiya.com/"+backimageurl;
					//detail.setBackimageurl("http://pic.bbyiya.com/"+detail.getBackimageurl()); 
				}
				String file_dir=basePath+sep+file_temp+sep+buyeruserid+"-"+producttile+"-"+propertystr.replaceAll("/", "-")+"×"+count+"("+userorderid+")";
			
				String fileFull_name=file_dir+sep+i+".jpg";
				i++;
				String filebackFull_name=file_dir+sep+i+".jpg";
				if(!FileUtils.isFileExists(fileFull_name)){
					try {
						if(imageurl!=null&&!imageurl.equals("null")&&!imageurl.equals("")){
							FileDownloadUtils.download(imageurl,fileFull_name);
							FileDownloadUtils.setDPI(fileFull_name);//修改正面图片的DPI
						}
						
						if(backimageurl!=null&&!backimageurl.equals("null")&&!backimageurl.equals("")){
							FileDownloadUtils.download(backimageurl,filebackFull_name);
							FileDownloadUtils.setDPI(filebackFull_name);//修改正面图片的DPI
							
							//如果是竖版的反面图，则需要旋转180度
							if(type!=1){		
								BufferedImage src = ImageIO.read(new File(filebackFull_name));
								BufferedImage des = RotateImage.Rotate(src, 180);
								ImageIO.write(des, "jpg", new File(filebackFull_name));
							}
							
							
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				i++;
				totalpro+=spacepro;
				progressbar.setValue((int)totalpro);
				
			}
			
		}
		return "1";
	}
}
