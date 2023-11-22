package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.Set;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        for (AbstractTerm i : termToPostingListMapping.keySet()) {
            tmp.append(i.toString()).append(", ");
            tmp.append(termToPostingListMapping.get(i).toString());
            tmp.append(", ");
        }
        tmp.deleteCharAt(tmp.length() - 1);
        tmp.deleteCharAt(tmp.length() - 1);
        return tmp.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        this.docIdToDocPathMapping.putIfAbsent(document.getDocId(), document.getDocPath());
        for (AbstractTermTuple i : document.getTuples()) {
            //已经存在这个词的情况
            if (this.termToPostingListMapping.get(i.term) != null) {
                AbstractPostingList curPostingList = this.termToPostingListMapping.get(i.term);
                //存在这个文档的情况
                if (curPostingList.indexOf(document.getDocId()) != -1) {
                    AbstractPosting curPosting = curPostingList.get(curPostingList.indexOf(document.getDocId()));
                    curPosting.setFreq(curPosting.getFreq() + 1);
                    curPosting.getPositions().add(i.curPos);
                }
                //不存在文档，新增一个Posting
                else {
                    AbstractPosting curPosting = new Posting();
                    curPosting.setDocId(document.getDocId());
                    curPosting.setFreq(1);
                    curPosting.getPositions().add(i.curPos);
                    curPostingList.add(curPosting);
                }
            }
            //不存在这个词，新增一个键值对
            else {
                AbstractPostingList curPostingList = new PostingList();
                AbstractPosting curPosting = new Posting();
                curPosting.setDocId(document.getDocId());
                curPosting.setFreq(1);
                curPosting.getPositions().add(i.curPos);
                curPostingList.add(curPosting);
                this.termToPostingListMapping.put(i.term, curPostingList);
            }
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try {
            this.readObject(new ObjectInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            this.writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return this.termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return this.termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for (AbstractTerm i : this.termToPostingListMapping.keySet()) {
            this.termToPostingListMapping.get(i).sort();
            for (int j = 0; j < this.termToPostingListMapping.get(i).size(); j++)
                this.termToPostingListMapping.get(i).get(j).sort();
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return this.docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            //Id->Path字典的序列化
            out.writeObject(docIdToDocPathMapping.size());
            for (int key : docIdToDocPathMapping.keySet()) {
                out.writeObject(key);
                out.writeObject(docIdToDocPathMapping.get(key));
            }
            //Term->PostingList字典的序列化
            out.writeObject(termToPostingListMapping.size());
            for (AbstractTerm key : termToPostingListMapping.keySet()) {
                key.writeObject(out);
                termToPostingListMapping.get(key).writeObject(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            //Id->Path字典的反序列化
            int tmpSize = (int) in.readObject();
            for (int i = 0; i < tmpSize; i++)
                docIdToDocPathMapping.putIfAbsent((int) in.readObject(), (String) in.readObject());
            //Term->PostingList字典的反序列化
            tmpSize = (int) in.readObject();
            for (int i = 0; i < tmpSize; i++) {
                AbstractTerm termTmp = new Term();
                AbstractPostingList postingListTmp = new PostingList();
                termTmp.readObject(in);
                postingListTmp.readObject(in);
                termToPostingListMapping.putIfAbsent(termTmp, postingListTmp);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
