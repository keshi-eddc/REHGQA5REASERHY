package com.edmi.site.cars.autohome.config;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * @author seamus
 * @date 2016年9月23日 上午11:50:13
 * @description
 */
public class HtmlDataUtil {

	/**
	 * 将产品的信息保存到本地磁盘
	 * @param pageNo
	 * @param urlContent
	 * @param category
	 * @param dataDir
	 */
	public static void saveData(int pageNo, String urlContent, String category, String dataDir) {
		File transactionFile = null;
		FileOutputStream outputStream = null;
		try {
			transactionFile = new File(dataDir + "/" + category);
			if (!transactionFile.exists()) {
				transactionFile.mkdirs();
			}
			transactionFile = new File(dataDir + "/" + category + "/page" + pageNo + ".txt");
			outputStream = new FileOutputStream(transactionFile);
			outputStream.write(urlContent.getBytes());
			outputStream.flush();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
			}
		}

	}

	/**
	 * 对某个文件集下的文件以页码的方式升序排列
	 * @param files 文件集合
	 */
	public static List<File> getFileSort(File[] files) {

		List<File> list = Arrays.asList(files);

		if (list != null && list.size() > 0) {

			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile) {
					int pageNum = Integer.parseInt(file.getName().substring(file.getName().indexOf("e") + 1, file.getName().indexOf(".")));
					int pageNum1 = Integer.parseInt(newFile.getName().substring(newFile.getName().indexOf("e") + 1, newFile.getName().indexOf(".")));
					if (pageNum > pageNum1) {
						return 1;
					} else if (pageNum == pageNum1) {
						return 0;
					} else {
						return -1;
					}
				}
			});

		}
		return list;
	}

	/**
	 * 对指定集合下的文件进行过滤
	 * @return 返回过滤规则
	 */
	public static FilenameFilter filterFiles() {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (!name.contains("page")) {
					return false;
				}
				return true;
			}
		};
		return filter;
	}

	/**
	 * 获取指定文件的内容
	 * @param file
	 * @return
	 */
	public static String getPathDatas(File file) {
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String lineInfo;
			while (null != (lineInfo = reader.readLine())) {
				buf.append(lineInfo);
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}

		return buf.toString();
	}

	/**
	 * 将指定信息保存到本地
	 * @param dir
	 * @param content
	 * @throws IOException 
	 */
	public static void saveData(String dir, String content) throws IOException {
		FileOutputStream outputStream = null;
		OutputStreamWriter osw = null;
		try {
			outputStream = new FileOutputStream(dir);
			osw = new OutputStreamWriter(outputStream, "GBK");
			osw.write(content);
			osw.flush();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		} catch (IOException e) {
			throw new IOException();
		} finally {
			try {
				osw.close();
				outputStream.close();
			} catch (IOException e) {
			}
		}

	}

	/**
	 * 将产品的信息保存到本地磁盘
	 * @param pageNo
	 * @param urlContent
	 * @param filePath
	 */
	public static void saveData(int pageNo, String urlContent, String filePath) {
		File transactionFile = null;
		FileOutputStream outputStream = null;
		try {
			transactionFile = new File(filePath);
			if (!transactionFile.exists()) {
				transactionFile.mkdirs();
			}
			transactionFile = new File(filePath + "/page" + pageNo + ".txt");
			outputStream = new FileOutputStream(transactionFile);
			outputStream.write(urlContent.getBytes());
			outputStream.flush();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
			}
		}
	}

	
	public static void deleteDir(File cataFile){
		if(cataFile.exists()){
			File[] files = cataFile.listFiles(); // 类目文件夹下的文件
			
			if(files.length > 0){
				for(File f : files){
					if(f.isDirectory()){ // 如果是分类
						deleteDir(f);
					}else{
						f.delete();
					}
				}
			}
		}
	}
	public static String saveFontData(String ttfurl,String dir) throws Exception
	{
		 String fileName = StringHelper.getResultByReg(ttfurl, "([^/]+)..ttf");
		 URL url = new URL("http:"+ttfurl);  
		  
         // 获取url链接反应  
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();  

         // 设置超时间为10秒  
         conn.setConnectTimeout(10 * 1000);  

         // 防止屏蔽程序抓取而返回403错误  
         conn.setRequestProperty("User-Agent",  
                 "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");  

         // 得到输入流  
         InputStream inputStream = conn.getInputStream();  

         ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
                  byte[] buff = new byte[100];  
                  int rc = 0;  
                  while ((rc = inputStream.read(buff, 0, 100)) > 0) {  
                      swapStream.write(buff, 0, rc);  
                  }  
         // 获取自己数组  
         byte[] getData =swapStream.toByteArray();  

         // 文件保存位置  
         File saveDir = new File(dir);  

         if (!saveDir.exists()) {  
             saveDir.mkdir();  
         }  
         String path = saveDir + File.separator +fileName+"..ttf";
         File file = new File(path);  
         FileOutputStream fos = new FileOutputStream(file);  
         fos.write(getData);  

         //得到ttf字的编码,猫眼电影中的ttf文件是对0到9的解析编码，找到其一一对应的顺序  

         if (fos != null) {  
             fos.close();  
         }  

         if (inputStream != null) {  
             inputStream.close();  
         }  
       return  path;
	}
}
