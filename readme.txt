
0. DESCRIPTION - 简述

    A simple crawler program to crawl news text from the website of People.cn (www.people.com.cn).
    简单的人民网(People.cn, www.people.com.cn)新闻文本爬虫。

	As the crawler invokes the Ansj Chinese NLP tool (https://github.com/NLPchina/ansj_seg), it can perform the word segmentation on the text collected just during the process of the crawling.
	由于调用了Ansj中文自然语言处理工具(https://github.com/NLPchina/ansj_seg)，该爬虫能够在爬取的过程中文成对中文文本的分词操作。

    Such program is written to collect a certain scale of Chinese text corpus, which was used for the application of Named Entity Recognition (NER), according to the requirement of a course's final project.
    这个爬虫程序是为了收集一定规模的中文语料用于命名实体识别(Named Entity Recognition, NER)的课程大作业而编写的。

    In the project, besides the source code of the crawler, I also provide an example Chinese news corpus, two datasets of Chinese vectors with different scale and a labeled dataset of Chinese Named Entity Recognition (NER).
    在这个项目中，除了爬虫源代码，笔者还提供了一个中文新闻示例语料，两个不同规模的中文词向量数据集和一个带标注的中文命名实体识别数据集。

    Details of the datasets will be introduced later.
    关于标注数据集的细节将在下文说明。

    Because of my limited capacity of programming, it's hard to avoid some some errors and deficiencies in the project. If you find some errors or anything that can be improved, you can contact me via [mengqin_az@foxmail.com]. Thank you very much!
    由于笔者水平有限，难免有疏漏之处，还望大家批评指正！如有关于源代码和数据集的任何问题，可通过邮件[mengqin_az@foxmail.com]联系，谢谢！


1. HOW TO USE - 使用方法
    1) Import the project; - 导入项目;

    2) Reload the Jar in the "lib" directory; - 重新加载"lib"文件夹中的Jar包;

    3) Set the parameters "timePre" (according to the date you want to crawl the news from) in program "src/PeopleCrawler.java"; - (根据待爬取的新闻日期)设置程序"src/PeopleCrawler.java"中变量"timePre"的值;
    (For example, if you want to crawl the news of 2017-12-01, you should set the "timePre" as "2017-12-01.html")
    (例如，当需要爬取2017-12-01的新闻，需要将变量"timePre"修改为"2017-12-04.html")

    4) Run the program "src/PeopleCrawler.java"; - 运行程序 "src/PeopleCrawler.java"; 

2. DESCRIPTION OF FILES - 项目文件说明
    ./lib/ -- The Jars that the program need to load; - 程序需要加载的Jar包;
    ./library/ --  The dictionary of the Ansj tool; - Ansj工具的字典;
	./src/ -- The source code of the crawler; - 爬虫源代码;
	./data/People/ -- Directory where the crawled text is saved; - 保存爬取的文本的文件夹;
    ./data/NER_data/ -- The labeled dataset of NER; - 标注的命名实体识别数据集;
	./data/People_example.rar  -- The example Chinese news corpus; - 示例英语新闻语料;
	./data/wordVec_ch/vectors(people_pku).txt -- A small scale dataset of Chinese word vectors 小规模中文词向量数据集
    (Trained by using the People's Daily Corpus(1998.01-1998.06), which is provided by the Institute of Computational Linguistics, Peking University)
    (使用由北京大学计算语言学研究所提供的人民日报语料库(1998.01-1998.06)训练)
    (Download link of the People's Daily corpus - 人民日报语料库下载链接： http://download.csdn.net/download/xufengye256/1784296)
	Chinese word vectors trained by using the People(pku) corpus
	./data/wordVec_ch/vectors(wiki+people_pku).txt -- A large scale dataset of Chinese word vectors 大规模的中文词向量数据集
	(Trained by using the hybrid corpus of People's Daily (pku_ver, 1998.01-1998.06) and Wiki)
    (使用人民日报和Wiki混合语料库训练)
    (Download link of Wiki corpus - Wiki语料库下载链接: https://github.com/reyoung/chinese_wiki_data_preprocessed)
    --Above datasets of Chinese word vectors are both trained by using GloVe.
    --上述中文词向量数据集均使用GloVe训练.
    (Download link of GloVe - GloVe下载链接: https://github.com/stanfordnlp/GloVe)


3. DESCRIPTION OF THE NER DATASET - 命名实体识别数据集说明 
    The dataset in such project is labeled by using the BRAT annotation tool(http://brat.nlplab.org/), and the result can be directly shown in the web-based GUI of BRAT.
    本项目的数据集使用BRAT标注工具(http://brat.nlplab.org/)进行标注，并可直接使用BRAT的Web界面查看标注结果。

    In the directory of the labeled dataset, annotation.conf is the definition file of the Name Entities, and such project is utilized for the application of the recognition of persons, locations and organizations.
    在标注的数据集文件夹下，annotation.conf是命名实体的定义文件，本项目主要用于人名、地名和组织名的命名实体识别应用.

    The definition of notations is follow.
    符号的定义如下所示。
      PER: Name of person - 人名
      LOC: Name of location - 地名
      ORG: Name of organization - 组织名
      MIST: Other named entity - 其他名字实体

    Every article is relvant ot one ".txt" file and one ".ann" file, where the .txt file is the original text and the ".ann" file is the label information of corresponding text.
    每篇新闻文本与一个".txt"文件和一个".ann"文件对应，其中".txt"文件是新闻的原文， 而".ann"文件是对应文本的标注信息。
