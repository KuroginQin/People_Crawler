//Program to crawl the news from www.people.com.cn
//从www.people.com.cn(人民网)爬取新闻文本的程序

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PeopleCrawler
{
	//Time prefix of of url; url中的时间前缀
	private String timePre = "2017-12-04.html";
	//Root page's url 根页面url
	private String rootUrl = "http://paperpost.people.com.cn/all-rmrb-" + timePre;
	//Url list of news pages 新闻页面的url列表
	private List<String> newsUrlList = new ArrayList<String>();
	//Counter of the number of news pages 新闻页面计数器
	private int newsPageCount = 0;
	
	public PeopleCrawler()
	{
		//Get the url list of news pages from the root page
		//从跟页面获取新闻页面的url列表
		getNewsUrlList(rootUrl);
		System.out.println("Total URLs: " + newsPageCount);
		for (int i=0;i<newsPageCount;i++)
		{
			String newsPageUrl = newsUrlList.get(i); //Url of current news page 当前新闻页面的url
			String newsText = getText(newsPageUrl); //Text of current news page 当前新闻页面的文本
			System.out.println("-News Text: " + newsText);
			//Save current news page's text 将当前新闻页面的文本保存至本地
			saveText(newsPageUrl , newsText);
		}
	}
	
	//Method to get the url list of news pages from different news sections
	//从不同新闻板块获取新闻页面url列表的方法
	private void getNewsUrlList(String rootUrl)
	{
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try
		{
			java.net.URL url = new java.net.URL(rootUrl); 
			//选择网页的字符集(utf-8/gbk/unicode)
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"utf-8"));
			String line;
			//逐行读取HTML文本
			while((line = in.readLine())!=null) 
				stringBuffer.append(line + "\r\n");
			str = stringBuffer.toString();
			//System.out.println(str);
			
			//Extract  the urls of news pages form the HTML text
			//从HTML文本中提取新闻页面的url
			String regex = "<a href=\".*?\"";
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
			Matcher match = pattern.matcher(str);
			String curUrl = "";
			while(match.find())
			{
				curUrl = match.group();
				curUrl = curUrl.replaceAll("<a href|\"|=", "");
				newsUrlList.add(curUrl);
				++newsPageCount;
				System.out.println(curUrl);
			}
			
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Method to extract the text from the news pages
	//从新闻页面提取文本的方法
	private String getText(String newsPageUrl)
	{
		String text = "";
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try
		{
			java.net.URL url = new java.net.URL(newsPageUrl); 
			//Select the Character set 选择网页的字符集(utf-8/gbk/unicode)
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"utf-8"));
			String line;
			//Read the HTML text line-by-line 逐行读取HTML文本
			while((line = in.readLine())!=null) 
				stringBuffer.append(line + "\r\n");
			str = stringBuffer.toString();
			//Extract text from the news page 从新闻页面中提取文本
			//Remove the redundant HTML tags 删除冗余的HTML标签
			str = str.replaceAll("(?is)<!DOCTYPE.*?>", "");
			str = str.replaceAll("(?is)<!--.*?-->", "");
			str = str.replaceAll("(?is)<script.*?>.*?</script>", "");
			str = str.replaceAll("(?is)<style.*?>.*?</style>", "");
			//Use Jsoup to extract specific content 使用Jsoup提取特定内容
			Document doc = Jsoup.parse(str);
			String title = ""; //Title of the news 新闻标题
			String content = ""; //Content of the news 新闻内容
			//Extract the title of the news 提取新闻标题
			title = doc.select("[class=text_c]>h1").first().text();
			//Extract the content of the news 提取新闻内容
			content = doc.select("[class=c_c]").toString();
			content = content.replaceAll("<.*?>|[　]|[  ]|&nbsp;","");
			content = content.replaceAll("SourcePh\"style=\"display:none\">", "");
			content = content.trim();
			System.out.println("Title: " + title);
			System.out.println("Content: " + content);
			//Perform the word segmentation process on the news' title
			//对新闻标题执行分词操作
			String titlePos = wordSeg(title);
			//System.out.println("Title: " + titlePos);
			//Perform the word segmentation process on the news' content
			//对新闻内容执行分析操作
			String[] contLines = content.split("\r\n");
			String contPos = "";
			for(int i=0;i<contLines.length;i++)
			{
				String curPosResult = wordSeg(contLines[i]);
				contPos += (curPosResult + "\r\n");
			}
			//System.out.println("Content: " + contPos);
			text = titlePos + "\r\n" + contPos;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return text;
	}
	
	//Method to perform the process of word segmentation
	//执行分词操作的方法
	private String wordSeg(String text)
	{
		String result = "";
		List<Term> term = ToAnalysis.parse(text).getTerms();  
		 for (int i=0;i<term.size();i++)
		 {
			 String curWord = term.get(i).getName();
			 result += (curWord + " ");
			 //System.out.println("Word: " + curWord);
		 }
		return result;
	}
	
	//Method to save text 
	//将文本保存至本地的方法
	private void saveText(String newsPageUrl, String newsText)
	{
		FileWriter fileWriter=null;
		try
		{
			//Extract current news' ID 提取当前新闻的识别号
			String regex = "renmrb.*?.htm";
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
			Matcher match = pattern.matcher(newsPageUrl);
			String newsId = ""; 
			if(match.find())
				newsId = match.group();
			newsId = newsId.replaceAll("[.]htm", "");
			System.out.println(newsId);
			fileWriter= new FileWriter(".//data//People//" + newsId+ ".txt"); 
			fileWriter.write(newsText);
			fileWriter.flush();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		finally
		{
			try
			{
				fileWriter.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[])
	{
		new PeopleCrawler();
	}
}