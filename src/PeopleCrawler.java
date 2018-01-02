//Program to crawl the news from www.people.com.cn
//��www.people.com.cn(������)��ȡ�����ı��ĳ���

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
	//Time prefix of of url; url�е�ʱ��ǰ׺
	private String timePre = "2017-12-04.html";
	//Root page's url ��ҳ��url
	private String rootUrl = "http://paperpost.people.com.cn/all-rmrb-" + timePre;
	//Url list of news pages ����ҳ���url�б�
	private List<String> newsUrlList = new ArrayList<String>();
	//Counter of the number of news pages ����ҳ�������
	private int newsPageCount = 0;
	
	public PeopleCrawler()
	{
		//Get the url list of news pages from the root page
		//�Ӹ�ҳ���ȡ����ҳ���url�б�
		getNewsUrlList(rootUrl);
		System.out.println("Total URLs: " + newsPageCount);
		for (int i=0;i<newsPageCount;i++)
		{
			String newsPageUrl = newsUrlList.get(i); //Url of current news page ��ǰ����ҳ���url
			String newsText = getText(newsPageUrl); //Text of current news page ��ǰ����ҳ����ı�
			System.out.println("-News Text: " + newsText);
			//Save current news page's text ����ǰ����ҳ����ı�����������
			saveText(newsPageUrl , newsText);
		}
	}
	
	//Method to get the url list of news pages from different news sections
	//�Ӳ�ͬ���Ű���ȡ����ҳ��url�б�ķ���
	private void getNewsUrlList(String rootUrl)
	{
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try
		{
			java.net.URL url = new java.net.URL(rootUrl); 
			//ѡ����ҳ���ַ���(utf-8/gbk/unicode)
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"utf-8"));
			String line;
			//���ж�ȡHTML�ı�
			while((line = in.readLine())!=null) 
				stringBuffer.append(line + "\r\n");
			str = stringBuffer.toString();
			//System.out.println(str);
			
			//Extract  the urls of news pages form the HTML text
			//��HTML�ı�����ȡ����ҳ���url
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
	//������ҳ����ȡ�ı��ķ���
	private String getText(String newsPageUrl)
	{
		String text = "";
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try
		{
			java.net.URL url = new java.net.URL(newsPageUrl); 
			//Select the Character set ѡ����ҳ���ַ���(utf-8/gbk/unicode)
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"utf-8"));
			String line;
			//Read the HTML text line-by-line ���ж�ȡHTML�ı�
			while((line = in.readLine())!=null) 
				stringBuffer.append(line + "\r\n");
			str = stringBuffer.toString();
			//Extract text from the news page ������ҳ������ȡ�ı�
			//Remove the redundant HTML tags ɾ�������HTML��ǩ
			str = str.replaceAll("(?is)<!DOCTYPE.*?>", "");
			str = str.replaceAll("(?is)<!--.*?-->", "");
			str = str.replaceAll("(?is)<script.*?>.*?</script>", "");
			str = str.replaceAll("(?is)<style.*?>.*?</style>", "");
			//Use Jsoup to extract specific content ʹ��Jsoup��ȡ�ض�����
			Document doc = Jsoup.parse(str);
			String title = ""; //Title of the news ���ű���
			String content = ""; //Content of the news ��������
			//Extract the title of the news ��ȡ���ű���
			title = doc.select("[class=text_c]>h1").first().text();
			//Extract the content of the news ��ȡ��������
			content = doc.select("[class=c_c]").toString();
			content = content.replaceAll("<.*?>|[��]|[  ]|&nbsp;","");
			content = content.replaceAll("SourcePh\"style=\"display:none\">", "");
			content = content.trim();
			System.out.println("Title: " + title);
			System.out.println("Content: " + content);
			//Perform the word segmentation process on the news' title
			//�����ű���ִ�зִʲ���
			String titlePos = wordSeg(title);
			//System.out.println("Title: " + titlePos);
			//Perform the word segmentation process on the news' content
			//����������ִ�з�������
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
	//ִ�зִʲ����ķ���
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
	//���ı����������صķ���
	private void saveText(String newsPageUrl, String newsText)
	{
		FileWriter fileWriter=null;
		try
		{
			//Extract current news' ID ��ȡ��ǰ���ŵ�ʶ���
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